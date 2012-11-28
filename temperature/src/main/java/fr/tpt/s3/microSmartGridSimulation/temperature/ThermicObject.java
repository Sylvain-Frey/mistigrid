package fr.tpt.s3.microSmartGridSimulation.temperature;

import fr.tpt.s3.microSmartGridSimulation.environment.Namable;

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
