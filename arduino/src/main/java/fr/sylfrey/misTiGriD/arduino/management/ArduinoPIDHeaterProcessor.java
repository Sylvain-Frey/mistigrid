package fr.sylfrey.misTiGriD.arduino.management;

public class ArduinoPIDHeaterProcessor {
	public float iterate(float currentOutput, float currentParameter) {
		
//		kp=(float)0.1;
//		ki=0;
//		
//		currentTemperature = currentOutput;
//		currentPower = currentParameter;
//
//		long now = System.currentTimeMillis();
//		long dt = now - lastUpdateTime/1000;
//
//		float error = requiredTemperature - currentTemperature;
//		
//		integral = Math.max(0,integral + error*dt);
//		integral = Math.min(maxPower/(10*ki),integral);
//		derivative = (error - lastError)/dt;
//
//				
//		float newPower = currentPower + kp*error + ki*integral + kd*derivative;
//
//		if (newPower<0) { newPower = 0; }
//		if (newPower>maxPower) { newPower = maxPower; }
//
//		lastError = error;
//		lastUpdateTime = now;
//		
//		System.out.println("# required = " + requiredTemperature + " : current = " + currentTemperature);
//		System.out.println("# " + kp*error + " " + ki*integral + " " + kd*derivative + " " + newPower + "\n");
//
//		
//		return newPower;
		
		long now = System.currentTimeMillis();
		float dt = (float)(now - lastUpdateTime)/1000;
		
		
		//WORKED MORE LESS OK WITH KP=2.7 & KD=0.5, THOUGH TEMP WAS LITTLE BIT BELLOW DESIRED ONE
//		kp=(float)3;
//		kd=(float)0.6;
//		ki=(float)0.001;
		
		kp=(float)0.8;
		kd=(float)3.0;
		ki=(float)0.0001;
		
		currentTemperature = currentOutput;
		currentPower = currentParameter;

		
		float error = requiredTemperature - currentTemperature;
		integral = integral + error;
		derivative = (error - lastError)/dt;
	
		float command=kp*error + kd*derivative + ki*integral*dt;
		
			
		if(accumulatePower){
			newPower=oldPower;
			
			if (maxPower*command<0){
				midPower += 0;
			}
			else if(maxPower*command>maxPower){
				midPower += maxPower;
			}	
			else{
				midPower+=maxPower*command;
			}
			
//			System.out.println(counter + ": " +midPower);
			counter++;
			if(counter==25){
				newPower=(float)midPower/25;
//				System.out.println("Ovo sad saljem: " + newPower);
//				System.out.println("Required temp: " + requiredTemperature);
//				System.out.println("Current temp: " + currentTemperature);
//				System.out.println("Error: " + error);
				counter=0;
				oldPower=newPower;
				midPower=0;
			}
		}else{
			
			newPower=maxPower*command;
			counter=0;
			accumulatePower=true;
			midPower=0;
			oldPower=newPower;
		}
		
		
		if (newPower<0) { newPower = 0; }
		if (newPower>maxPower) { newPower = maxPower; }

		
//		System.out.println("# required = " + requiredTemperature + " : current = " + currentTemperature);
//		System.out.println("# " + kp + " " + error + " " + kp*error);
//		System.out.println("# " + kd + " " + derivative + " " + kd*derivative);
//		System.out.println(command*maxPower + "=" + newPower + "\n");
//
		
		lastError = error;
		lastUpdateTime = now;		
		
		return newPower;		
	}

	public float requiredTemperature = 20;
	public float maxPower = 0f;
	
//	public float kp = maxPower/10;
	public float kp = 100;
//	public float kp = maxPower;
//	public float ki = (float) 1E-13;
	public float ki = 0;
	public float kd = 0;
	
	public float currentTemperature = 20;	
	public float currentPower = 0f;

	private float lastError = 0f;
	private float integral = 0f;
	private float derivative = 0f;
	private long lastUpdateTime;
	
	private float newPower;
	private float midPower=0;
	private float oldPower=0;
	private boolean accumulatePower=false;
	private int counter=0;

}
