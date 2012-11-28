package fr.tpt.s3.cirrus.entity;

public interface ActiveComposite<State> extends Active<State>, FiltroSubscribable<State> {
	
	public State get(Filter<State> filter);	
	
}
