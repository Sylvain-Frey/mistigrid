package fr.sylfrey.misTiGriD.webGUI;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.osgi.service.http.NamespaceException;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.event.EventBus;
import fr.sylfrey.akka.ActorSystemProvider;
import fr.sylfrey.iris.webSockets.WebSocketHandler;
import fr.sylfrey.iris.webSockets.WebSocketProvider;
import fr.sylfrey.misTiGriD.trace.Archiver;
import fr.sylfrey.misTiGriD.trace.ArchiverImpl.ArchiverEvent;

@Component(name="Traces",immediate=true)
@Provides(specifications= {WebSocketHandler.class})
@Instantiate
public class TracesImpl implements /*Traces,*/ WebSocketHandler {

	@Requires
	public WebSocketProvider webSocketProvider;
	
//	@Requires
//	JsonRpcProvider jsonRpcProvider;
	
	@Bind
	public void bindActorSystem(ActorSystemProvider actorSystemProvider) {
		this.actorSystem = actorSystemProvider.getSystem();
	}
	private ActorSystem actorSystem;
	
	@Bind
	public void bindArchiver(Archiver archiver) {
		this.bus = archiver.bus();
	}
	private EventBus bus;
	
	@Validate
	public void start() {		
		try {
//			jsonRpcProvider.publish("traces", this, Traces.class);
			webSocketProvider.publish("/traces", this);
		} catch (NamespaceException e) {
			e.printStackTrace();
		}
		subscriber = actorSystem.actorOf(
				new Props(new UntypedActorFactory() {
					private static final long serialVersionUID = 1L;
					public UntypedActor create() {
						return new ArchiveSubscriber(subscribers);
					}
				}));
		bus.subscribe(subscriber, ArchiverEvent.class);
	}
	
	@Invalidate
	public void stop() {
		webSocketProvider.unpublish("/traces");
		bus.unsubscribe(subscriber);
		actorSystem.stop(subscriber);
//		jsonRpcProvider.unpublish("traces");
	}
	
	private Collection<Channel> subscribers = new ConcurrentLinkedQueue<Channel>();
	
	@Override
	public void handleWebSocketFrame(ChannelHandlerContext ctx,
			WebSocketFrame frame) {
		String frameText = ((TextWebSocketFrame) frame).getText();
		if (frameText.equals("subscribe")) {
//			System.out.println("# new subscriber");
			subscribers.add(ctx.getChannel());
			ctx.getChannel().write(new TextWebSocketFrame("# subscription OK"));
		} else if (frameText.equals("unsubscribe")) {
//			System.out.println("# subscriber leaves");
			subscribers.remove(ctx.getChannel());
			ctx.getChannel().write(new TextWebSocketFrame("# unsubscription OK"));
			ctx.getChannel().close();
		}
//		ctx.getChannel().write(new TextWebSocketFrame(answer));
	}
	
//	@Override
//	public String[] listTraces() {
//		List<String> traces = new LinkedList<String>();
//		for (File trace : baseDirFile.listFiles()) {
//			if (trace.getName().endsWith(".log")) {
//				traces.add(trace.getName());
//			}
//		}
//		String[] result = new String[traces.size()];
//		return traces.toArray(result);
//	}

//	@Override
//	public String[][] data(String traceName) {
//		File log = new File(baseDirName + File.separator + traceName);
//		if (!log.exists()){
//			return null;
//		}
//		List<String[]> listResult = new LinkedList<String[]>();
//		try {
//			BufferedReader br = new BufferedReader(new FileReader(log));
//			String line = null;  
//			while ((line = br.readLine()) != null) { 
//				listResult.add(line.split(","));				
//			}
//			String[][] result = new String[listResult.size()][];
//			return listResult.toArray(result);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//			return null;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		}						
//	}
	
//	private final String baseDirName = "log"; // cf. ...microSmartGridSimulation.trace.CSVTracer
//	private final File baseDirFile = new File(baseDirName);

	private ActorRef subscriber;

}
