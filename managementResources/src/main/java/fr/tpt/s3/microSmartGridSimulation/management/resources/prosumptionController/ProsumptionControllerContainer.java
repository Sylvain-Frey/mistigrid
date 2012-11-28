package fr.tpt.s3.microSmartGridSimulation.management.resources.prosumptionController;

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
import fr.tpt.s3.akka.ActorSystemProvider;
import fr.tpt.s3.microSmartGridSimulation.electricalGrid.Prosumer;
import fr.tpt.s3.microSmartGridSimulation.environment.Updatable;
import fr.tpt.s3.microSmartGridSimulation.framework.ActorContainer;

@Component(name="ProsumptionController",immediate=true)
@Provides(specifications={ActorContainer.class,Updatable.class})
public class ProsumptionControllerContainer implements ActorContainer<ProsumptionController>, Updatable {
	
	@Validate
	public void start() {
		controller = TypedActor.get(actorSystem).typedActorOf(
				new TypedProps<ProsumptionControllerImpl>(
						ProsumptionController.class, 
						new Creator<ProsumptionControllerImpl>() {
							public ProsumptionControllerImpl create() {
								return new ProsumptionControllerImpl(prosumer,maxConsumption);
							}}),
							actorPath);
	}
	
	@Override
	public ProsumptionController actor() {
		return controller;
	}

	@Override
	public int getPeriod() {
		return period;
	}

	@Override
	public void update() {
		controller.update();
	}
	
	@Invalidate
	public void stop() {
		TypedActor.get(actorSystem).stop(controller);
	}
	
	@Property(mandatory=true)
	public String actorPath;

	@Property(mandatory=true)
	public int period;
	
	@Property(mandatory=true)
	public float maxConsumption;
	
	@Requires
	public Prosumer prosumer;

	@Bind
	public void bindActorSystem(ActorSystemProvider provider) {
		actorSystem = provider.getSystem();
	}
	
	public ActorSystem actorSystem;	
	
	private ProsumptionController controller;

}
