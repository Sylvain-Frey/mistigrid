package fr.sylfrey.misTiGriD.electricalGrid.impl;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import fr.sylfrey.misTiGriD.electricalGrid.Aggregator;
import fr.sylfrey.misTiGriD.electricalGrid.BlackOut;
import fr.sylfrey.misTiGriD.electricalGrid.Prosumer;
import fr.sylfrey.misTiGriD.electricalGrid.RemoteAggregator;

public class RemoteAggregatorImpl implements RemoteAggregator {

	public RemoteAggregatorImpl(ActorSystem actorSystem, RemoteAggregator parent, String name) {
		this.actorSystem = actorSystem;
		this.delegate = new AggregatorImpl(parent, name);
	}
	
	@Override
	public float getProsumedPower() {
		return delegate.getProsumedPower();
	}

	@Override
	public String getName() {
		return delegate.getName();
	}

	@Override
	public void connect(ActorRef prosumer) {
		delegate.connect(get(prosumer));
	}

	@Override
	public void disconnect(ActorRef prosumer) {
		delegate.disconnect(get(prosumer));
	}

	@Override
	public void blackout() {
		delegate.blackout();
	}

	@Override
	public void updateProsumption(ActorRef prosumer, Float prosumption) throws BlackOut {
		delegate.updateProsumption(get(prosumer), prosumption);
	}

	@Override
	public float getAggregatedPowerConsumption() {
		return delegate.getAggregatedPowerConsumption();
	}

	@Override
	public float getAggregatedPowerProduction() {
		return delegate.getAggregatedPowerProduction();
	}

	@Override
	public float getBill() {
		return delegate.getBill();
	}
	
	
	private Prosumer get(ActorRef ref) {
		return TypedActor.get(actorSystem).typedActorOf(
				new TypedProps<Prosumer>(Prosumer.class),ref);
	}
	
	private Aggregator delegate;
	public ActorSystem actorSystem;
	
}