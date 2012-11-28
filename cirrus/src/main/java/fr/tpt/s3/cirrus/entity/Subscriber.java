package fr.tpt.s3.cirrus.entity;

public interface Subscriber<D> {
	
	public void notify(D data);

}
