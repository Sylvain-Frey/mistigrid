package fr.sylfrey.misTiGriD.trace;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;

import akka.event.EventBus;
import akka.event.EventStream;
import fr.sylfrey.misTiGriD.alba.basic.roles.HouseLoadManager;
import fr.sylfrey.misTiGriD.electricalGrid.Prosumer;
import fr.sylfrey.misTiGriD.environment.Updatable;
//import fr.sylfrey.misTiGriD.framework.ActorContainer;
//import fr.sylfrey.misTiGriD.management.resources.loadHierarch.LoadHierarch;
import fr.sylfrey.misTiGriD.temperature.ThermicObject;

@Component(name="Archiver", immediate=true)
@Provides
public class ArchiverImpl implements Updatable, Archiver {

	@Property
	public int period = 1000;

	@Requires
	public Tracer tracer;


	@Bind(aggregate=true, optional=true)
	public void bindThermicObject(ThermicObject to) {
		tracer.createValueLog("temperature_" + to.getName());
		thermicObjects.add(to);
	}
	private List<ThermicObject> thermicObjects = new LinkedList<ThermicObject>();


	@Bind(aggregate=true, optional=true)
	public void bindProsumer(Prosumer prosumer) {
		tracer.createValueLog("prosumption_" + prosumer.getName());
		prosumers.add(prosumer);
	}
	private Collection<Prosumer> prosumers = new ConcurrentLinkedQueue<Prosumer>();

	@Bind(id="loadManager", optional=true)
	public void bindLoadManager(HouseLoadManager houseLoadManager) {
		this.houseLoadManager = houseLoadManager;
		tracer.createValueLog("loadManager_maxConsumptionThreshold");
	}

	@Unbind(id="loadManager", optional=true)
	public void unbindLoadManager(HouseLoadManager houseLoadManager) {
		houseLoadManager = null;
	}

	private HouseLoadManager houseLoadManager;


	private EventBus bus = new EventStream(false);


	@Override
	public int getPeriod() {
		return period;
	}

	@Override
	public void update() {
		for (ThermicObject to : thermicObjects) {
			tracer.logValue("temperature_" + to.getName(), to.getCurrentTemperature());
			bus.publish(new ArchiverEvent<Float>("temperature_" + to.getName(), to.getCurrentTemperature()));
		}
		for (Prosumer prosumer : prosumers) {
			tracer.logValue("prosumption_" + prosumer.getName(), prosumer.getProsumedPower());
			bus.publish(new ArchiverEvent<Float>("prosumption_" + prosumer.getName(), prosumer.getProsumedPower()));			
		}
		if (houseLoadManager != null) {
			tracer.logValue("loadManager_maxConsumptionThreshold", houseLoadManager.maxConsumptionThreshold());
			bus.publish(new ArchiverEvent<Float>("loadManager_maxConsumptionThreshold", houseLoadManager.maxConsumptionThreshold()));
		}
	}

	@Override
	public EventBus bus() {
		return bus;
	}

	public class ArchiverEvent<Content> {
		public final String type;
		public final Content content;
		public ArchiverEvent(String type, Content content) {
			this.type = type;
			this.content = content;
		}
		@Override public String toString() {
			return "{type : " + type + ", content : " + content + "}";
		}
	}

}
