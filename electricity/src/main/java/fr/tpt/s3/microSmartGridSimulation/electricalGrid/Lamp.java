package fr.tpt.s3.microSmartGridSimulation.electricalGrid;


public interface Lamp extends TunableProsumer, OnOffProsumer {

	/**
	 * @return the electrical power currently consumed 
	 * by this Lamp, in Watts.
	 */
	public float getEmissionPower();
	
	/**
	 * @return the maximum this Lamp can consume.
	 */
	public float getMaxEmissionPower();
	
	/**
	 * @param power: how much this Lamp consumes, in Watts.
	 */
	public void setEmissionPower(float power);
	
}
