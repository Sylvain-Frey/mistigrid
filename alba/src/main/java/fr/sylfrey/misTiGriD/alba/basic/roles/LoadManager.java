package fr.sylfrey.misTiGriD.alba.basic.roles;

import akka.actor.ActorRef;
//import fr.sylfrey.misTiGriD.alba.basic.messages.ProsumerStatus;

public interface LoadManager {

	public void register(ActorRef prosumer);
	public void unregister(ActorRef prosumer);

	public float maxConsumptionThreshold();
	public void setMaximumProsumption(float threshold);
//	public void setStatus(ProsumerStatus status);

}
