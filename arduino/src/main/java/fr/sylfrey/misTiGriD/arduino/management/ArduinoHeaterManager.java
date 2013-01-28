package fr.sylfrey.misTiGriD.arduino.management;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import akka.actor.ActorSystem;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import akka.japi.Creator;
import fr.sylfrey.akka.ActorSystemProvider;
import fr.sylfrey.misTiGriD.appliances.Heater;
import fr.sylfrey.misTiGriD.environment.Updatable;
import fr.sylfrey.misTiGriD.framework.ActorContainer;
import fr.sylfrey.misTiGriD.framework.topic.Topic;
import fr.sylfrey.misTiGriD.management.data.LoadMessage;
import fr.sylfrey.misTiGriD.management.resources.loadHierarch.LoadHierarch;
import fr.sylfrey.misTiGriD.management.resources.monolithicHeaterManager.HeaterManager;
import fr.sylfrey.misTiGriD.management.resources.monolithicHeaterManager.MonolithicHeaterManager;
import fr.sylfrey.misTiGriD.management.resources.monolithicHeaterManager.MonolithicHeaterManagerImpl;
import fr.sylfrey.misTiGriD.management.resources.prosumptionController.ProsumptionController;
import fr.sylfrey.misTiGriD.temperature.ThermicObject;

@Component(name="ArduinoHeaterManager",immediate=true)
@Provides(specifications={Updatable.class,HeaterManager.class})
public class ArduinoHeaterManager implements Updatable, HeaterManager {

	@Validate
	public void start() {
		manager = TypedActor.get(actorSystem).typedActorOf(
				new TypedProps<MonolithicHeaterManagerImpl>(
						MonolithicHeaterManager.class, 
						new Creator<MonolithicHeaterManagerImpl>() {
							public MonolithicHeaterManagerImpl create() {
								return new MonolithicHeaterManagerImpl(room, heater, controller, requiredTemperature, isCollaborative);
							}}),
							actorPath);
		/* Collaboration */
		loadTopic.subscribe(manager);
		/* Hierarchy */
		hierarch.register(manager);
	}
	
	@Override
	public void update() {
		manager.update();
	}
	
	@Override
	public int getPeriod() {
		return period;
	}
	
	@Override
	public boolean isEconomizing() {
		return manager.isEconomizing();
	}
	
	public float getRequiredTemperature() {
		return manager.getRequiredTemperature();
	}

	public void setRequiredTemperature(float requiredTemperature) {
		manager.setRequiredTemperature(requiredTemperature);
	}
	
	@Invalidate
	public void stop() {
		/* Collaboration */
		loadTopic.unsubscribe(manager);
		/* Hierarchy */
		hierarch.unregister(manager);
		TypedActor.get(actorSystem).stop(manager);
	}

	@Requires(id="room")
	public ThermicObject room;
	
	@Requires(id="heater")
	public Heater heater;

	/* Controller */
	@Bind(id="controller")
	public void bindController(ActorContainer<ProsumptionController> container) {
		controller = container.actor();
	}
	
	private ProsumptionController controller;
	
	/* Collaboration */
	@Property(mandatory=true)
	public boolean isCollaborative;	
	
	@Bind(id="loadTopic")
	public void bindTopic(ActorContainer<Topic<LoadMessage>> container) {
		loadTopic = container.actor();
	}
	
	private Topic<LoadMessage> loadTopic;
	
	
	/* Hierarchy */
	@Bind(id="hierarch")
	public void bindHierarch(ActorContainer<LoadHierarch> container) {
		hierarch = container.actor();
	}
	
	private LoadHierarch hierarch;

	@Bind
	public void bindActorSystem(ActorSystemProvider provider) {
		actorSystem = provider.getSystem();
	}

	public ActorSystem actorSystem;	
	
	
	@Property(mandatory=true)
	public float requiredTemperature;
	
	@Property(mandatory=true)
	public int period;

	@Property(mandatory=true)
	public String actorPath;	
	
	private MonolithicHeaterManager manager;
	
}
