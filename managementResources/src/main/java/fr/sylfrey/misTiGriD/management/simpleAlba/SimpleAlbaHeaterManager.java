package fr.sylfrey.misTiGriD.management.simpleAlba;

import akka.actor.ActorPath;
import fr.sylfrey.misTiGriD.appliances.Heater;
import fr.sylfrey.misTiGriD.environment.Updatable;
import fr.sylfrey.misTiGriD.management.data.Load;
import fr.sylfrey.misTiGriD.management.data.LoadMessage;
import fr.sylfrey.misTiGriD.management.resources.monolithicHeaterManager.MonolithicHeaterManager;
import fr.sylfrey.misTiGriD.management.resources.pID.PIDHeaterProcessor;
import fr.sylfrey.misTiGriD.temperature.ThermicObject;

public class SimpleAlbaHeaterManager implements MonolithicHeaterManager, Updatable {

	public SimpleAlbaHeaterManager(Heater heater, ThermicObject room,
			ActorPath selfPath, float requiredTemperature, int period,
			boolean isCollaborative) {
		super();
		this.heater = heater;
		this.room = room;
		this.selfPath = selfPath;
		this.requiredTemperature = requiredTemperature;
		this.period = period;
		this.isCollaborative = isCollaborative;
		this.pid = new PIDHeaterProcessor();
		this.isCollaborative = isCollaborative;
		pid.requiredTemperature = requiredTemperature;
		pid.maxPower = heater.getMaxEmissionPower();
		pid.kp = pid.maxPower/10;
	}

	@Override
	public int getPeriod() {
		return period;
	}
	
	@Override
	public void update() {

		currentPower = heater.getEmissionPower();

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

		heater.setEmissionPower(newPower);
		
	}

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
	
	public float requiredTemperature;
	public PIDHeaterProcessor pid;

	public int period;

	public float kp = 0.1f;
	public float ki = (float) 1E-14;
	public float kd = 0;
	public float currentPower = 0f;

	public Load load = Load.STD;
	public boolean isCollaborative;
	public boolean isEconomizing;
}
