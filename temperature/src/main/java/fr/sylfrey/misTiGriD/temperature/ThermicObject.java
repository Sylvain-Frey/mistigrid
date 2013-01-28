package fr.sylfrey.misTiGriD.temperature;

import fr.sylfrey.misTiGriD.environment.Namable;

/**
 * A thermic object has a temperature and exchanges heat 
 * with its neighbours through Walls.
 * @author syl
 *
 */
public interface ThermicObject extends Namable {
	
	/**
	 * @return the current temperature of this ThermiObject, in °C.
	 */
    public float getCurrentTemperature();
    
}
