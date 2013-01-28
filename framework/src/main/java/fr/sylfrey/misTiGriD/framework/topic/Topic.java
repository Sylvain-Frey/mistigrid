package fr.sylfrey.misTiGriD.framework.topic;

import fr.sylfrey.misTiGriD.framework.Consumer;
import fr.sylfrey.misTiGriD.framework.Publisher;

public interface Topic<Message> extends Publisher<Message>, Consumer<Message> {

	public void subscribe(Consumer<Message> subscriber);
	public void unsubscribe(Consumer<Message> subscriber);

	public void tell(Message message);

}
