package fr.tpt.s3.cirrus.entity;

public interface Subscribable<Data> {
	
	public void subscribe(Subscriber<Data> subscriber);
	public void unsubscribe(Subscriber<Data> subscriber);

}
