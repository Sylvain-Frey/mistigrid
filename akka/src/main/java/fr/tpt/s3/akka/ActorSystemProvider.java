package fr.tpt.s3.akka;

import akka.actor.ActorSystem;

public interface ActorSystemProvider {

	public ActorSystem getSystem();
	
}
