package fr.tpt.s3.microSmartGridSimulation.appliances;

import fr.tpt.s3.microSmartGridSimulation.electricalGrid.TunableProsumer;
import fr.tpt.s3.microSmartGridSimulation.temperature.ThermicObject;
import fr.tpt.s3.microSmartGridSimulation.temperature.Wall;

/**
 * Describes a heater, that is, a ThermicObject which temperature is determined
 * by tuning its electrical consumption (cf. TunableProsumer) 
 * and that influences its thermic neighbours (cf. Wall).
 * @author syl
 *
 */
public interface Heater extends Wall, ThermicObject, TunableProsumer {

	/**
	 * @return the electrical power currently consumed 
	 * by this Heater, in Watts. How this power is converted 
	 * into temperature is a matter of implementation and efficiency.
	 */
	public float getEmissionPower();
	
	/**
	 * @return the maximum this Heater can consume (and therefore heat).
	 */
	public float getMaxEmissionPower();
	
	/**
	 * @param power: how much this Heater consumes, in Watts.
	 */
	public void setEmissionPower(float power);
	
}
