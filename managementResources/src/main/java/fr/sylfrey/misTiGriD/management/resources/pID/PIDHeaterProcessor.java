package fr.sylfrey.misTiGriD.management.resources.pID;

public class PIDHeaterProcessor {

	public float iterate(float currentOutput, float currentParameter) {
		
		currentTemperature = currentOutput;
		currentPower = currentParameter;

		long now = System.currentTimeMillis();
		long dt = now - lastUpdateTime/1000;

		float error = requiredTemperature - currentTemperature;
		integral = Math.max(0,integral + error*dt);
		integral = Math.min(maxPower/(10*ki),integral);
		derivative = (error - lastError)/dt;

		float newPower = currentPower + kp*error + ki*integral + kd*derivative;

		if (newPower<0) { newPower = 0; }
		if (newPower>maxPower) { newPower = maxPower; }

		lastError = error;
		lastUpdateTime = now;
		
		return newPower;
		
	}

	public float requiredTemperature = 20;
	public float maxPower = 0f;
	
	public float kp = maxPower/10;
	public float ki = (float) 1E-13;
	public float kd = 0;
	
	public float currentTemperature = 20;	
	public float currentPower = 0f;

	private float lastError = 0f;
	private float integral = 0f;
	private float derivative = 0f;
	private long lastUpdateTime;
	
}
