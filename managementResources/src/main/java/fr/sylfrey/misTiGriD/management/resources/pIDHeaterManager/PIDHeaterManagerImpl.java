package fr.sylfrey.misTiGriD.management.resources.pIDHeaterManager;

import akka.actor.ActorPath;
import akka.actor.TypedActor;
import fr.sylfrey.misTiGriD.appliances.Heater;
import fr.sylfrey.misTiGriD.framework.Consumer;
import fr.sylfrey.misTiGriD.framework.data.Permission;
import fr.sylfrey.misTiGriD.framework.data.PermissionRequest;
import fr.sylfrey.misTiGriD.management.data.ProsumptionChange;
import fr.sylfrey.misTiGriD.management.data.TemperatureChange;
import fr.sylfrey.misTiGriD.management.resources.prosumptionController.ProsumptionController;

public class PIDHeaterManagerImpl implements PIDHeaterManager<TemperatureChange>, Consumer<TemperatureChange> {

	@Override
	public void tell(TemperatureChange temperatureChange) {

		currentTemperature = temperatureChange.newValue;

		currentPower = heater.getEmissionPower();
		maxPower = heater.getMaxEmissionPower();
		kp = maxPower/10;

		long now = System.currentTimeMillis();
		long dt = now - lastUpdateTime/1000;

		float error = requiredTemperature - currentTemperature;
		integral = Math.max(0,integral + error*dt);
		integral = Math.min(maxPower/ki,integral);
		derivative = (error - lastError)/dt;

		float newPower = currentPower + kp*error + ki*integral + kd*derivative;

		if (newPower<0) { newPower = 0; }
		if (newPower>maxPower) { newPower = maxPower; }

		Permission<ProsumptionChange> permission = 
				controller.ask(new PermissionRequest<ProsumptionChange>(
								selfPath, 
								new ProsumptionChange(selfPath, currentPower, newPower)));
		if (permission.isGranted) {		
			heater.setEmissionPower(newPower);
		}
		
		lastError = error;
		lastUpdateTime = now;

//		System.out.println("# required = " + requiredTemperature + " : current = " + currentTemperature);
//		System.out.println("# " + kp*error + " " + ki*integral + " " + kd*derivative + " " + newPower);

	}

	public PIDHeaterManagerImpl(Heater heater, 
			ProsumptionController controller, float requiredTemperature) {
		super();
		this.heater = heater;
		this.controller = controller;
		this.requiredTemperature = requiredTemperature;
		this.selfPath = TypedActor.context().self().path();
	}

	public Heater heater;

	public float requiredTemperature = 20;

	@Override
	public float getRequiredTemperature() {
		return requiredTemperature;
	}

	@Override
	public void setRequiredTemperature(float requiredTemperature) {
		this.requiredTemperature = requiredTemperature;
	}

	public float kp = 1f;
	public float ki = (float) 1E-13;
	public float kd = 0;

	private float currentTemperature = 20;	
	private float currentPower = 0f;
	private float maxPower = 0f;

	private float lastError = 0f;
	private float integral = 0f;
	private float derivative = 0f;
	private long lastUpdateTime;

	private ActorPath selfPath;

	private ProsumptionController controller;
	
}
