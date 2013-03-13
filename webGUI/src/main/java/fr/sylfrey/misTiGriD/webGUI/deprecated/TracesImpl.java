package fr.sylfrey.misTiGriD.webGUI.deprecated;

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
	}
	
	private Collection<Channel> subscribers = new ConcurrentLinkedQueue<Channel>();
	
	@Override
	public void handleWebSocketFrame(ChannelHandlerContext ctx,
			WebSocketFrame frame) {
		String frameText = ((TextWebSocketFrame) frame).getText();
		if (frameText.equals("subscribe")) {
			subscribers.add(ctx.getChannel());
			ctx.getChannel().write(new TextWebSocketFrame("# subscription OK"));
		} else if (frameText.equals("unsubscribe")) {
			subscribers.remove(ctx.getChannel());
			ctx.getChannel().write(new TextWebSocketFrame("# unsubscription OK"));
			ctx.getChannel().close();
		}
	}
	
	private ActorRef subscriber;

}
