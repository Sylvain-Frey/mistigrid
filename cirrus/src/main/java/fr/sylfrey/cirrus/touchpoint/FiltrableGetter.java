package fr.sylfrey.cirrus.touchpoint;

public interface FiltrableGetter<State> extends Getter<State> {
	
	public State get(Filter<State> filter);	
	
}
