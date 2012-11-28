package fr.tpt.s3.microSmartGridSimulation.electricalGrid.impl;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.service.http.NamespaceException;

import akka.actor.ActorSystem;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import akka.japi.Creator;
import fr.tpt.s3.akka.ActorSystemProvider;
import fr.tpt.s3.iris.akka.AkkaProvider;
import fr.tpt.s3.microSmartGridSimulation.electricalGrid.Prosumer;
import fr.tpt.s3.microSmartGridSimulation.electricalGrid.RemoteAggregator;

@Component(name="RemoteAggregator")
@Provides()
public class RemoteAggregatorContainer implements Prosumer {
	
	@Validate
	public void start() { 
		if (hasRemoteParent && remoteParentURL!=null) {
			try {
				parent = remote.get(remoteParentURL, RemoteAggregator.class);
				System.out.println("# connected to " + parent.getName() + " aka "+ parent.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			parent = null;
		}
		delegate = TypedActor.get(actorSystem).typedActorOf(
				new TypedProps<RemoteAggregatorImpl>(
						RemoteAggregator.class, 
						new Creator<RemoteAggregatorImpl>() {
							public RemoteAggregatorImpl create() {
								return new RemoteAggregatorImpl(actorSystem,parent,name);
							}}),
							actorPath);
		if (parent!=null) {
			parent.connect(TypedActor.get(actorSystem).getActorRefFor(delegate));
		}
		try {
			remote.publish("remote" + actorPath, delegate, RemoteAggregator.class);
		} catch (NamespaceException e) {
			e.printStackTrace();
		}
	}
	
	@Invalidate
	public void stop() {
		if (parent!=null) {
			parent.disconnect(TypedActor.get(actorSystem).getActorRefFor(delegate));
		}
		TypedActor.get(actorSystem).stop(delegate);
	}

	
	@Property(name="instance.name")
	public String name;
	
	@Property
	public String actorPath;

	@Property
	public boolean hasRemoteParent;
	
	@Property
	public String remoteParentURL;
	
	@Bind
	public void bindActorSystem(ActorSystemProvider provider) {
		actorSystem = provider.getSystem();
	}

	public ActorSystem actorSystem;
	
	@Requires
	AkkaProvider remote;
	
	private RemoteAggregator delegate;
	
	private RemoteAggregator parent;

	@Override
	public String getName() {
		return delegate.getName();
	}

	@Override
	public float getProsumedPower() {
		return delegate.getProsumedPower();
	}

	@Override
	public void blackout() {
		delegate.blackout();
	}
	
}
