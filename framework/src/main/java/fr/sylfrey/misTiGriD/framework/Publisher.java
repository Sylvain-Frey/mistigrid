package fr.sylfrey.misTiGriD.framework;

public interface Publisher<Message> {
	
	public void subscribe(Consumer<Message> subscriber);
	public void unsubscribe(Consumer<Message> subscriber);

}
