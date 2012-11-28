package fr.tpt.s3.microSmartGridSimulation.framework.topic;

import fr.tpt.s3.microSmartGridSimulation.framework.Consumer;
import fr.tpt.s3.microSmartGridSimulation.framework.Publisher;

public interface Topic<Message> extends Publisher<Message>, Consumer<Message> {

	public void subscribe(Consumer<Message> subscriber);
	public void unsubscribe(Consumer<Message> subscriber);

	public void tell(Message message);

}
