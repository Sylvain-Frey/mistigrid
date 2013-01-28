package fr.sylfrey.misTiGriD.arduino;

import fr.sylfrey.misTiGriD.temperature.ThermicObject;


public interface ArduinoThermicObject extends ThermicObject {
	
	/**
	 * ArduinoSerialReader call this as a dispatcher and sets right temperature in each room
	 * @param temperature
	 */
	public void setCurrentTemperature(float temperature);
	
}
