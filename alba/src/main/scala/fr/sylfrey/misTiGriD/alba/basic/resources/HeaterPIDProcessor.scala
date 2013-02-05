package fr.sylfrey.misTiGriD.alba.basic.resources

class HeaterPIDProcessor(
    var requiredTemperature : Float,
	var maxPower : Float,	
	var currentTemperature : Float,	
	var currentPower : Float,
	var kp : Float,
	var ki : Float,
	var kd : Float	
) {
  

  var lastError : Float = 0f
  var integral : Float = 0f
  var derivative : Float = 0f
  var lastUpdateTime : Long = 0L
  
  def iterate(currentOutput : Float, currentParameter : Float) : Float =  {
		
		currentTemperature = currentOutput;
		currentPower = currentParameter;

		val now = System.currentTimeMillis();
		val dt = now - lastUpdateTime/1000;

		val error = requiredTemperature - currentTemperature;
		integral = Math.max(0,integral + error*dt);
		integral = Math.min(maxPower/(10*ki),integral);
		derivative = (error - lastError)/dt;

		var newPower = currentPower + kp*error + ki*integral + kd*derivative;

		if (newPower<0) { newPower = 0; }
		if (newPower>maxPower) { newPower = maxPower; }

		lastError = error;
		lastUpdateTime = now;
		
		return newPower;
		
	}
  
}