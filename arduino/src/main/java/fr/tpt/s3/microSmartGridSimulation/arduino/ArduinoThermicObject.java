package fr.tpt.s3.microSmartGridSimulation.arduino;

import fr.tpt.s3.microSmartGridSimulation.temperature.ThermicObject;


public interface ArduinoThermicObject extends ThermicObject {
	
	/**
	 * ArduinoSerialReader call this as a dispatcher and sets right temperature in each room
	 * @param temperature
	 */
	public void setCurrentTemperature(float temperature);
	
}
