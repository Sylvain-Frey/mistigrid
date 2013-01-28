package fr.sylfrey.misTiGriD.management.resources.loadHierarch;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import fr.sylfrey.misTiGriD.electricalGrid.Aggregator;
import fr.sylfrey.misTiGriD.framework.Consumer;
import fr.sylfrey.misTiGriD.management.data.LoadMessage;

public class RemoteLoadHierarchImpl implements RemoteLoadHierarch {

	public RemoteLoadHierarchImpl(ActorSystem actorSystem, Aggregator aggregator, float maxConsumption) {
		delegate = new LoadHierarchImpl(aggregator, maxConsumption);
		this.actorSystem = actorSystem;
	}
	
	@Override
	public void tell(LoadMessage msg) {
		delegate.tell(msg);
	}

	@Override
	public void register(ActorRef loadManager) {
		delegate.register(get(loadManager));
	}

	@Override
	public void unregister(ActorRef loadManager) {
		delegate.unregister(get(loadManager));
	}

	@Override
	public void update() {
		delegate.update();
	}

	@Override
	public float maxConsumptionThreshold() {
		return delegate.maxConsumptionThreshold();
	}

	private Consumer<LoadMessage> get(ActorRef ref) {
		return TypedActor.get(actorSystem).typedActorOf(
				new TypedProps<Consumer>(Consumer.class),ref);
	}
	
	private LoadHierarch delegate;
	public ActorSystem actorSystem;

}
