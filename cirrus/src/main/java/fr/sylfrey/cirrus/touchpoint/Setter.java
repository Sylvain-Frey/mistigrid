package fr.sylfrey.cirrus.touchpoint;

public interface Setter<State> extends Effector<State> {
	
	public void set(State state);

}
