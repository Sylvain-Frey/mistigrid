package fr.tpt.s3.microSmartGridSimulation.management.resources.monolithicHeaterManager;

import akka.actor.ActorPath;
import akka.actor.TypedActor;
import fr.tpt.s3.microSmartGridSimulation.appliances.Heater;
import fr.tpt.s3.microSmartGridSimulation.management.data.Load;
import fr.tpt.s3.microSmartGridSimulation.management.data.LoadMessage;
import fr.tpt.s3.microSmartGridSimulation.management.resources.pID.PIDHeaterProcessor;
import fr.tpt.s3.microSmartGridSimulation.management.resources.prosumptionController.ProsumptionController;
import fr.tpt.s3.microSmartGridSimulation.temperature.ThermicObject;

public class MonolithicHeaterManagerImpl implements MonolithicHeaterManager {

	public MonolithicHeaterManagerImpl(ThermicObject room, Heater heater, 
			ProsumptionController controller, float requiredTemperature, boolean isCollaborative) {
		super();
		this.room = room;
		this.heater = heater;
		this.controller = controller;
		this.requiredTemperature = requiredTemperature;
		this.selfPath = TypedActor.context().self().path();
		this.pid = new PIDHeaterProcessor();
		this.isCollaborative = isCollaborative;
		pid.requiredTemperature = requiredTemperature;
		pid.maxPower = heater.getMaxEmissionPower();
		pid.kp = pid.maxPower/10;
	}

	@Override
	public void update() {

		currentPower = heater.getEmissionPower();

		/* Collaboration, Hierarchy */
		if (isCollaborative) {
			if (load == Load.HIGH) {
				pid.requiredTemperature = requiredTemperature - 2.0f;
				isEconomizing = true;
			} else {
				pid.requiredTemperature = requiredTemperature;
				isEconomizing = false;
			}
		}

		float newPower = pid.iterate(room.getCurrentTemperature(), currentPower);

		if (newPower==currentPower) { return; }

		/* Controller */
		//		Permission<ProsumptionChange> permission = 
		//				controller.ask(new PermissionRequest<ProsumptionChange>(
		//								selfPath, 
		//								new ProsumptionChange(
		//										selfPath, 
		//										currentPower, 
		//										newPower)));

		//		if (permission.isGranted) {		
		heater.setEmissionPower(newPower);
		//		}// else {
		//			heater.setEmissionPower(0f);
		//		}

		//		System.out.println("# required = " + requiredTemperature + " : current = " + currentTemperature);
		//		System.out.println("# " + kp*error + " " + ki*integral + " " + kd*derivative + " " + newPower);

	}

	/* Collaboration*/
	@Override
	public void tell(LoadMessage msg) {
		this.load = msg.load;
	}

	@Override
	public float getRequiredTemperature() {
		return pid.requiredTemperature;
	}

	@Override
	public void setRequiredTemperature(float requiredTemperature) {
		this.requiredTemperature = requiredTemperature;
		this.pid.requiredTemperature = requiredTemperature;
	}
	
	@Override
	public boolean isEconomizing() {
		return isEconomizing;
	}

	public Heater heater;
	public ThermicObject room;
	public ActorPath selfPath;
	/* Controller */
	public ProsumptionController controller;	
	public float requiredTemperature;
	public PIDHeaterProcessor pid;


	public float kp = 0.1f;
	public float ki = (float) 1E-14;
	public float kd = 0;
	private float currentPower = 0f;

	/* Collaboration */
	private Load load;
	private boolean isCollaborative;
	private boolean isEconomizing;

}
