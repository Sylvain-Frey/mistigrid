package fr.tpt.s3.microSmartGridSimulation.management.resources.loadHierarch;

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
import fr.tpt.s3.microSmartGridSimulation.electricalGrid.Aggregator;
import fr.tpt.s3.microSmartGridSimulation.environment.Updatable;
import fr.tpt.s3.microSmartGridSimulation.framework.ActorContainer;

@Component(name="LoadHierarch",immediate=true)
@Provides(specifications={Updatable.class,ActorContainer.class})
public class LoadHierarchContainer implements Updatable, ActorContainer<LoadHierarch> {
	
	@Validate
	public void start() {
		hierarch = TypedActor.get(actorSystem).typedActorOf(
				new TypedProps<LoadHierarchImpl>(
						LoadHierarch.class, 
						new Creator<LoadHierarchImpl>() {
							public LoadHierarchImpl create() {
								return new LoadHierarchImpl(aggregator,highThreshold);
							}}),
							actorPath);
	}
	
	@Override
	public int getPeriod() {
		return period;
	}

	@Override
	public void update() {
		hierarch.update();		
	}
	
	@Override
	public LoadHierarch actor() {
		return hierarch;
	}
	
	@Invalidate
	public void stop() {
		TypedActor.get(actorSystem).stop(hierarch);
	}
	
	@Property(mandatory=true)
	public String actorPath;

	@Property(mandatory=true)
	public int period;
	
	@Property(mandatory=true)
	public float highThreshold;
	
	@Requires
	public Aggregator aggregator;

	@Bind
	public void bindActorSystem(ActorSystemProvider provider) {
		actorSystem = provider.getSystem();
	}
	
	public ActorSystem actorSystem;	
	
	private LoadHierarch hierarch;

}
