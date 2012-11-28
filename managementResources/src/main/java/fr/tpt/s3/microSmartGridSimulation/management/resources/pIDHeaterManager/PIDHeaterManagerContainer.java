package fr.tpt.s3.microSmartGridSimulation.management.resources.pIDHeaterManager;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
//import org.apache.felix.ipojo.handlers.jmx.Config;
//import org.apache.felix.ipojo.handlers.jmx.Method;

import akka.actor.ActorSystem;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import akka.japi.Creator;
import fr.tpt.s3.akka.ActorSystemProvider;
import fr.tpt.s3.microSmartGridSimulation.appliances.Heater;
import fr.tpt.s3.microSmartGridSimulation.framework.ActorContainer;
import fr.tpt.s3.microSmartGridSimulation.framework.topic.Topic;
import fr.tpt.s3.microSmartGridSimulation.management.data.TemperatureChange;
import fr.tpt.s3.microSmartGridSimulation.management.resources.prosumptionController.ProsumptionController;

@Component(name="PIDHeaterManager",immediate=true)
public class PIDHeaterManagerContainer {
	
	@Validate
	public void start() {
		manager = TypedActor.get(actorSystem).typedActorOf(
				new TypedProps<PIDHeaterManagerImpl>(
						PIDHeaterManager.class, 
						new Creator<PIDHeaterManagerImpl>() {
							public PIDHeaterManagerImpl create() {
								return new PIDHeaterManagerImpl(heater, controller, requiredTemperature);
							}}),
							actorPath);
		topic.subscribe(manager);
	}
	
	@Invalidate
	public void stop() {
		if (topic!=null) {
			topic.unsubscribe(manager);	
		}		
		TypedActor.get(actorSystem).stop(manager);
	}
	
	@Requires
	public Heater heater;

	@Bind(id="topic")
	public void bindTopic(ActorContainer<Topic<TemperatureChange>> container) {
		topic = container.actor();
	}
	
	private Topic<TemperatureChange> topic;	

	@Bind(id="controller")
	public void bindController(ActorContainer<ProsumptionController> container) {
		controller = container.actor();
	}
	
	private ProsumptionController controller;
	
	@Property(mandatory=true)
	public float requiredTemperature = 20;

	@Property(mandatory=true)
	public String actorPath;

	@Bind
	public void bindActorSystem(ActorSystemProvider provider) {
		actorSystem = provider.getSystem();
	}

	public ActorSystem actorSystem;	
	
	public float getRequiredTemperature() {
		return manager.getRequiredTemperature();
	}

	public void setRequiredTemperature(float requiredTemperature) {
		manager.setRequiredTemperature(requiredTemperature);
	}
	
	private PIDHeaterManager<TemperatureChange> manager;
	
}
