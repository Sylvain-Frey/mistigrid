package fr.tpt.s3.alba.simple.implementation;

import fr.tpt.s3.alba.simple.messages.LoadBalancingOrder;
import fr.tpt.s3.alba.simple.messages.Prosumption;
import fr.tpt.s3.alba.simple.roles.Prosumer;
import fr.tpt.s3.alba.simple.roles.ProsumerStatus;
import fr.tpt.s3.cirrus.agent.R;
import fr.tpt.s3.cirrus.organisation.Status;
import fr.tpt.s3.cirrus.touchpoint.Sensor;

public class HeaterManager implements Prosumer {


	@Override
	public R<Status<ProsumerStatus>> prosumerStatus() {
		return prosumerStatusRef;
	}

	@Override
	public R<Sensor<Prosumption>> prosumptionSensor() {
		return prosumptionSensorRef;
	}
	

	@Override
	public void tell(LoadBalancingOrder order) {
		// TODO Auto-generated method stub
		
	}

	
	public R<Prosumer> self;
	
	public Status<ProsumerStatus> prosumerStatus;
	private R<Status<ProsumerStatus>> prosumerStatusRef;
	
	public Sensor<Prosumption> prosumptionSensor;
	private R<Sensor<Prosumption>> prosumptionSensorRef;

}
