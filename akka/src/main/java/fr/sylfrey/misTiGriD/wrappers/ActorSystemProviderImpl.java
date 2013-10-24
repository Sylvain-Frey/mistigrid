package fr.sylfrey.misTiGriD.wrappers;

import java.io.File;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import akka.actor.ActorSystem;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

@Component
@Provides
@Instantiate(name="ActorSystemProvider")
public class ActorSystemProviderImpl implements ActorSystemProvider {

	@Requires BundleContextProvider bcProvider;

	@Validate
	public void start() {
		delegateSystem = config();
		bcProvider.get().registerService(ActorSystemProvider.class.getName(), this, null);
		System.out.println("# actor system running and available : " + delegateSystem.name());
	}

	@Invalidate
	public void stop() {
		delegateSystem.shutdown();
		System.out.println("# actor system stopped");
	}
	
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
	
}
