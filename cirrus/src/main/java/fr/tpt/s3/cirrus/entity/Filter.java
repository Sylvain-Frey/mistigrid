package fr.tpt.s3.cirrus.entity;

public interface Filter<Data> {

	public boolean filter(Data data);
	
}
