package fr.sylfrey.iris;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.service.http.NamespaceException;

import fr.sylfrey.iris.akka.AkkaProvider;


@Component(immediate=true)
//@Instantiate
public class Test {
	
	@Requires
	AkkaProvider remote;
	
	@Validate
	public void start() throws InterruptedException, NamespaceException, NotFoundException {
		Thread.sleep(2000);
		Ping ping = new Ping() {			
			@Override
			public String ping(String msg) {
				return "# " + msg;
			}
		};
		remote.publish("ping", ping, Ping.class);		
		Thread.sleep(2000);
//		Ping remotePing = remote.get("http://localhost:8080/jsonrpc/ping", Ping.class);
		Ping remotePing = remote.get("akka://MisTiGriD@localhost:4004/user/ping", Ping.class);
		Thread.sleep(2000);
		System.out.println("# " + remotePing.ping("plop"));
	}
	
	@Invalidate
	public void stop() {
		remote.unpublish("ping");
	}

}
