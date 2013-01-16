package fr.tpt.s3.akka;

import org.osgi.framework.BundleActivator;

import akka.actor.ActorSystem;

public interface ActorSystemProvider extends BundleActivator {

	public ActorSystem getSystem();
	
}
