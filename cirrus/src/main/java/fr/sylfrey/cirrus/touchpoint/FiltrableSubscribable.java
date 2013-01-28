package fr.sylfrey.cirrus.touchpoint;

public interface FiltrableSubscribable<Data> extends Subscribable<Data> {

	public void subscribe(Consumer<Data> subscriber, Filter<Data> filter);
	
}
