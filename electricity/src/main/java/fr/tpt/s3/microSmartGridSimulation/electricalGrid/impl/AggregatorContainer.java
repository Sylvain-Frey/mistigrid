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
import fr.tpt.s3.microSmartGridSimulation.electricalGrid.Aggregator;
import fr.tpt.s3.microSmartGridSimulation.electricalGrid.BlackOut;
import fr.tpt.s3.microSmartGridSimulation.electricalGrid.Prosumer;
import fr.tpt.s3.microSmartGridSimulation.electricalGrid.RemoteAggregator;

@Component(name="Aggregator")
@Provides(specifications={Aggregator.class,Prosumer.class})
public class AggregatorContainer implements Aggregator, Prosumer {

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
		aggregator = TypedActor.get(actorSystem).typedActorOf(
				new TypedProps<AggregatorImpl>(
						Aggregator.class, 
						new Creator<AggregatorImpl>() {
							public AggregatorImpl create() {
								return new AggregatorImpl(parent,name);
							}}),
							actorPath);
		if (parent!=null) {
			parent.connect(TypedActor.get(actorSystem).getActorRefFor(aggregator));
		}
		try {
			remote.publish("remote" + actorPath, aggregator, Aggregator.class);
		} catch (NamespaceException e) {
			e.printStackTrace();
		}
	}
	
	@Invalidate
	public void stop() {
		if (parent!=null) {
			parent.disconnect(TypedActor.get(actorSystem).getActorRefFor(aggregator));
		}
		TypedActor.get(actorSystem).stop(aggregator);
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
	
	private Aggregator aggregator;

	@Override
	public String getName() {
		return aggregator.getName();
	}

	@Override
	public void connect(Prosumer prosumer) {
		aggregator.connect(prosumer);
	}

	@Override
	public void disconnect(Prosumer prosumer) {
		aggregator.disconnect(prosumer);
	}

	@Override
	public void updateProsumption(Prosumer prosumer, float prosumption)
			throws BlackOut {
		aggregator.updateProsumption(prosumer, prosumption);
	}

	@Override
	public float getAggregatedPowerConsumption() {
		return aggregator.getAggregatedPowerConsumption();
	}

	@Override
	public float getAggregatedPowerProduction() {
		return aggregator.getAggregatedPowerProduction();
	}

	@Override
	public float getBill() {
		return aggregator.getBill();
	}

	@Override
	public float getProsumedPower() {
		return aggregator.getProsumedPower();
	}

	@Override
	public void blackout() {
		aggregator.blackout();
	}
	
	private RemoteAggregator parent;

}
