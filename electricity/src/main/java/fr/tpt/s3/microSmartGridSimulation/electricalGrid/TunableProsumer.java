package fr.tpt.s3.microSmartGridSimulation.electricalGrid;

/**
 * A Prosumer with a tunable prosumed power. 
 * @author syl
 *
 */
public interface TunableProsumer extends Prosumer {
	
	/**
	 * @param power : the electrical power (in Watt) prosumed by this appliance.
	 */
	public void setProsumedPower(float power);
	
}
