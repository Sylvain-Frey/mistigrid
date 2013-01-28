package fr.sylfrey.cirrus.touchpoint;

public interface Subscribable<State> extends Sensor<State> {
	
	public void subscribe(Consumer<State> subscriber);
	public void unsubscribe(Consumer<State> subscriber);

}
