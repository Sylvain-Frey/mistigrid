package fr.tpt.s3.akka;

import java.io.File;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import akka.actor.ActorSystem;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ActorSystemProviderImpl implements ActorSystemProvider, BundleActivator {

	@Override
	public ActorSystem getSystem() {
		return delegateSystem;
	}

	private ActorSystem delegateSystem;
	
	private ActorSystem config() {
		File configFile = new File("conf/akka.conf");
		Config config = ConfigFactory.parseFile(configFile);
		ClassLoader cl = ActorSystemProviderImpl.class.getClassLoader();
		String userName = config.getString("akka.userName");
		return ActorSystem.create(userName, config, cl);
	}

	@Override
	public void start(BundleContext context) throws Exception {
		context.registerService(ActorSystemProvider.class.getName(), this, null);
		delegateSystem = config();
	}

	@Override
	public void stop(BundleContext context) throws Exception {}
	
}
