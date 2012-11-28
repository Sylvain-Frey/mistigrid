package fr.tpt.s3.cirrus.entity;

public interface Publishable<Data> {
	
	public void publish(Data data);

}
