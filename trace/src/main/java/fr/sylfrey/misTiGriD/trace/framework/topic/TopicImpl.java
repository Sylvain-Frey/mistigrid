package fr.sylfrey.misTiGriD.trace.framework.topic;

import java.util.LinkedList;
import java.util.List;

import fr.sylfrey.misTiGriD.trace.framework.Consumer;

public class TopicImpl<Msg> implements Topic<Msg> {

	@Override
	public void subscribe(Consumer<Msg> subscriber) {
		if (subscriber==null) { return; }
		subscribers.add(subscriber);
	}

	@Override
	public void unsubscribe(Consumer<Msg> subscriber) {
		if (subscriber==null) { return; }
		subscribers.remove(subscriber);
	}

	@Override
	public void tell(Msg message) {
		for (Consumer<Msg> subscriber : subscribers) {
			subscriber.tell(message);
//			System.out.println("# topic told " + subscriber.toString());
		}		
	}
	
	private List<Consumer<Msg>> subscribers = new LinkedList<Consumer<Msg>>();

}
