package fr.tpt.s3.akka;

import java.io.File;

import org.osgi.framework.BundleContext;

import akka.actor.ActorSystem;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ActorSystemProviderImpl implements ActorSystemProvider {

	@Override
	public ActorSystem getSystem() {
		return delegateSystem;
	}

	private ActorSystem delegateSystem;
	
	private ActorSystem config() {
		
		File configFile = new File("conf/akka.conf");
		File refConfigFile = new File("conf/reference.conf");
		
		Config refConfig = ConfigFactory.parseFile(refConfigFile);
		Config config = ConfigFactory.parseFile(configFile).withFallback(refConfig);
		
		ClassLoader cl = ActorSystem.class.getClassLoader();
		String userName = config.getString("akka.userName");
		
		return ActorSystem.create(userName, config, cl);
	}

	@Override
	public void start(BundleContext context) throws Exception {
		delegateSystem = config();
		context.registerService(ActorSystemProvider.class.getName(), this, null);
		System.out.println("# actor system running and available : " + delegateSystem.name());
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		delegateSystem.shutdown();
		System.out.println("# actor system stopped");
	}
	
}
