package fr.sylfrey.misTiGriD.management.data;

import akka.actor.ActorPath;
import fr.sylfrey.misTiGriD.framework.data.AttributeChange;

public class TemperatureChange extends AttributeChange<Float> {

	public TemperatureChange(ActorPath sender, float oldTemperature,float newTemperature) {
		super(sender, oldTemperature, newTemperature);
	}

	private static final long serialVersionUID = 1L;	

}
