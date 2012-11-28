package fr.tpt.s3.microSmartGridSimulation.trace;

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
import fr.tpt.s3.microSmartGridSimulation.electricalGrid.Prosumer;
import fr.tpt.s3.microSmartGridSimulation.environment.Updatable;
import fr.tpt.s3.microSmartGridSimulation.framework.ActorContainer;
import fr.tpt.s3.microSmartGridSimulation.management.resources.loadHierarch.LoadHierarch;
import fr.tpt.s3.microSmartGridSimulation.temperature.ThermicObject;

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

	@Bind(id="hierarch", optional=true)
	public void bindHierarch(ActorContainer<LoadHierarch> container) {
		loadHierarch = container.actor();
		tracer.createValueLog("loadHierarch_maxConsumptionThreshold");
	}

	@Unbind(id="hierarch", optional=true)
	public void unbindHierarch(ActorContainer<LoadHierarch> container) {
		loadHierarch = null;
	}

	private LoadHierarch loadHierarch;


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
		if (loadHierarch != null) {
			tracer.logValue("loadHierarch_maxConsumptionThreshold", loadHierarch.maxConsumptionThreshold());
			bus.publish(new ArchiverEvent<Float>("loadHierarch_maxConsumptionThreshold", loadHierarch.maxConsumptionThreshold()));
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