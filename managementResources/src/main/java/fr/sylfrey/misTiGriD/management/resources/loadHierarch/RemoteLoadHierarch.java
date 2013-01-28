package fr.sylfrey.misTiGriD.management.resources.loadHierarch;

import akka.actor.ActorRef;
import fr.sylfrey.misTiGriD.framework.Consumer;
import fr.sylfrey.misTiGriD.management.data.LoadMessage;

public interface RemoteLoadHierarch extends Consumer<LoadMessage> {

	public void register(ActorRef loadManager);
	public void unregister(ActorRef loadManager);
	public void update();
	public float maxConsumptionThreshold();
	
}
