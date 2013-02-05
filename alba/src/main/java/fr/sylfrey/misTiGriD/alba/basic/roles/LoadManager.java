package fr.sylfrey.misTiGriD.alba.basic.roles;

import akka.actor.ActorRef;

public interface LoadManager {
	
	  public void register(ActorRef prosumer);
	  public void unregister(ActorRef prosumer);

}
