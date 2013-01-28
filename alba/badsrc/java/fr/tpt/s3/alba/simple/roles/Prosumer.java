package fr.tpt.s3.alba.simple.roles;

import fr.tpt.s3.alba.simple.messages.LoadBalancingOrder;
import fr.tpt.s3.alba.simple.messages.Prosumption;
import fr.tpt.s3.cirrus.agent.R;
import fr.tpt.s3.cirrus.organisation.Status;
import fr.tpt.s3.cirrus.organisation.hierarchy.Subordinate;
import fr.tpt.s3.cirrus.touchpoint.Sensor;

public interface Prosumer extends Subordinate<LoadBalancingOrder> { 
	 
	public R<Status<ProsumerStatus>> prosumerStatus();
	public R<Sensor<Prosumption>> prosumptionSensor();

}
