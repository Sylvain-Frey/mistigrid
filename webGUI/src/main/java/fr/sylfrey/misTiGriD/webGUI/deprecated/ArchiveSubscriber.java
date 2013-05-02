package fr.sylfrey.misTiGriD.webGUI.deprecated;

import java.util.Collection;
import java.util.LinkedList;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import akka.actor.UntypedActor;
import fr.sylfrey.misTiGriD.trace.ArchiverEvent;

public class ArchiveSubscriber extends UntypedActor {

	private Collection<Channel> subscribers;
	private LinkedList<Channel> blackList = new LinkedList<Channel>();

	public ArchiveSubscriber(Collection<Channel> subscribers) {
		this.subscribers = subscribers;
	}

	@Override
	public void onReceive(Object o) throws Exception {
		if (!(o instanceof ArchiverEvent)) {
			System.out.println("# improper event " + o.toString() + " in ArchiveSubscriber.");
		}
		
		ArchiverEvent<?> ae = (ArchiverEvent<?>) o;
		ObjectNode data = mapper.createObjectNode();
		data.put("type", ae.type);
		if (ae.content instanceof Float) {
			data.put("content", (Float) ae.content);
		} else {
			data.put("content", ae.content.toString());
		} 

		while (!blackList.isEmpty()) subscribers.remove(blackList.poll());

		for (Channel  channel : subscribers) {				
			if (!channel.isOpen()) {
//				System.out.println("# subscriber gone...");
				blackList.add(channel);
			} else try {
				channel.write(new TextWebSocketFrame(data.toString()));
			} catch (Exception e) {
//				System.out.println("# lost subscriber...");
				blackList.add(channel);
				channel.close();
			}
		}		
	}		

	private ObjectMapper mapper = new ObjectMapper();

}