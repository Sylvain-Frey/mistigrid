package fr.sylfrey.misTiGriD.arduino.management;

import akka.actor.ActorPath;
import akka.actor.TypedActor;
import fr.sylfrey.misTiGriD.appliances.Heater;
import fr.sylfrey.misTiGriD.management.data.Load;
import fr.sylfrey.misTiGriD.management.data.LoadMessage;
import fr.sylfrey.misTiGriD.management.resources.monolithicHeaterManager.MonolithicHeaterManager;
import fr.sylfrey.misTiGriD.management.resources.prosumptionController.ProsumptionController;
import fr.sylfrey.misTiGriD.temperature.ThermicObject;

public class ArduinoHeaterManagerImpl implements MonolithicHeaterManager {

	public ArduinoHeaterManagerImpl(ThermicObject room, Heater heater, 
			ProsumptionController controller, float requiredTemperature, boolean isCollaborative) {
		super();
		this.room = room;
		this.heater = heater;
		this.controller = controller;
		this.requiredTemperature = requiredTemperature;
		this.selfPath = TypedActor.context().self().path();
		this.pid = new ArduinoPIDHeaterProcessor();
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
				pid.requiredTemperature = requiredTemperature - 2;
				isEconomizing = true;
			} else {
				pid.requiredTemperature = requiredTemperature;
				isEconomizing = false;
			}
		}
//		pid.requiredTemperature = requiredTemperature;

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
		System.out.println("I just set NEW POWER: " + newPower);
		//		}// else {
		//			heater.setEmissionPower(0f);
		//		}

//				System.out.println("# required = " + requiredTemperature + " : current = " + currentTemperature);
//				System.out.println("# " + kp*error + " " + ki*integral + " " + kd*derivative + " " + newPower);

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
	public boolean isEconomizing() {
		return isEconomizing;
	}

	
	@Override
	public void setRequiredTemperature(float requiredTemperature) {
		this.requiredTemperature = requiredTemperature;
		this.pid.requiredTemperature = requiredTemperature;
	}

	public Heater heater;
	public ThermicObject room;
	public ActorPath selfPath;
	/* Controller */
	public ProsumptionController controller;
	public float requiredTemperature;
	public ArduinoPIDHeaterProcessor pid;


	public float kp = 0.1f;
	public float ki = (float) 1E-14;
	public float kd = 0;
	private float currentPower = 0f;

	/* Collaboration */
	private Load load;
	private boolean isCollaborative;
	private boolean isEconomizing;

}
