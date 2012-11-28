package fr.tpt.s3.cirrus.entity;

public interface Active<State> extends Subscribable<State> {

	public State get();
	
}
