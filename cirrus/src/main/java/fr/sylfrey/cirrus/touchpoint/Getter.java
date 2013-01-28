package fr.sylfrey.cirrus.touchpoint;

public interface Getter<State> extends Sensor<State> {

	public State get();
	
}
