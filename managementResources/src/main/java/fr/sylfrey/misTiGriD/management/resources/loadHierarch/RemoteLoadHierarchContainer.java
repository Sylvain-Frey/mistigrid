package fr.sylfrey.misTiGriD.management.resources.loadHierarch;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.service.http.NamespaceException;

import akka.actor.ActorSystem;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import akka.japi.Creator;
import fr.sylfrey.akka.ActorSystemProvider;
import fr.sylfrey.iris.akka.AkkaProvider;
import fr.sylfrey.misTiGriD.electricalGrid.Aggregator;

@Component(name="RemoteLoadHierarch")
public class RemoteLoadHierarchContainer {
	
	@Validate
	public void start() { 
		delegate = TypedActor.get(actorSystem).typedActorOf(
				new TypedProps<RemoteLoadHierarchImpl>(
						RemoteLoadHierarch.class, 
						new Creator<RemoteLoadHierarchImpl>() {
							public RemoteLoadHierarchImpl create() {
								return new RemoteLoadHierarchImpl(actorSystem, aggregator, maxConsumption);
							}}),
							actorPath);
		try {
			remote.publish("remote" + actorPath, delegate, RemoteLoadHierarch.class);
		} catch (NamespaceException e) {
			e.printStackTrace();
		}
	}
	
	@Invalidate
	public void stop() {
		TypedActor.get(actorSystem).stop(delegate);
	}

	@Property(name="instance.name")
	public String name;
	
	@Property
	public String actorPath;

	@Property
	public float maxConsumption;
	
	@Requires
	public Aggregator aggregator;

	@Requires
	public AkkaProvider remote;
		
	@Bind
	public void bindActorSystem(ActorSystemProvider provider) {
		actorSystem = provider.getSystem();
	}	
	
	public ActorSystem actorSystem;
	
	private RemoteLoadHierarch delegate;

}
