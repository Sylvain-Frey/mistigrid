package fr.tpt.s3.cirrus.entity;

public interface FiltroSubscribable<Data> extends Subscribable<Data> {

	public void subscribe(Subscriber<Data> subscriber, Filter<Data> filter);
	
}
