package fr.tpt.s3.microSmartGridSimulation.management.data;

import akka.actor.ActorPath;
import fr.tpt.s3.microSmartGridSimulation.framework.data.AttributeChange;

public class TemperatureChange extends AttributeChange<Float> {

	public TemperatureChange(ActorPath sender, float oldTemperature,float newTemperature) {
		super(sender, oldTemperature, newTemperature);
	}

	private static final long serialVersionUID = 1L;	

}
