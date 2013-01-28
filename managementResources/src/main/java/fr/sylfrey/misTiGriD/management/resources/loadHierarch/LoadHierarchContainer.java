package fr.sylfrey.misTiGriD.management.resources.loadHierarch;

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
import fr.sylfrey.iris.akka.AkkaProvider;
import fr.sylfrey.misTiGriD.electricalGrid.Aggregator;
import fr.sylfrey.misTiGriD.environment.Updatable;
import fr.sylfrey.misTiGriD.framework.ActorContainer;

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
		if (hasRemoteParent && remoteParentURL!=null) {
			try {
				districtHierarch = remote.get(remoteParentURL, RemoteLoadHierarch.class);
				districtHierarch.register(TypedActor.get(actorSystem).getActorRefFor(hierarch));
				System.out.println("# registering to district hierarch aka " + districtHierarch.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
		districtHierarch.unregister(TypedActor.get(actorSystem).getActorRefFor(hierarch));
		TypedActor.get(actorSystem).stop(hierarch);
	}
	
	@Property(mandatory=true)
	public String actorPath;

	@Property(mandatory=true)
	public int period;
	
	@Property(mandatory=true)
	public float highThreshold;
	
	@Property
	public boolean hasRemoteParent;
	
	@Property
	public String remoteParentURL;
	
	@Requires
	public Aggregator aggregator;

	@Requires
	public AkkaProvider remote;
	
	@Bind
	public void bindActorSystem(ActorSystemProvider provider) {
		actorSystem = provider.getSystem();
	}
	
	public ActorSystem actorSystem;	
	
	private LoadHierarch hierarch;
	
	private RemoteLoadHierarch districtHierarch;

}
