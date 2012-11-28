package fr.tpt.s3.microSmartGridSimulation.management.data;

import akka.actor.ActorPath;
import fr.tpt.s3.microSmartGridSimulation.framework.data.AttributeChange;

public class ProsumptionChange extends AttributeChange<Float> {
	
	public ProsumptionChange(ActorPath sender, float oldPower, float newPower) {
		super(sender, oldPower, newPower);
	}

	private static final long serialVersionUID = 1L;	
	
}
