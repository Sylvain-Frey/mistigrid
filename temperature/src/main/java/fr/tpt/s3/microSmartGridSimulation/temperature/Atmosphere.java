package fr.tpt.s3.microSmartGridSimulation.temperature;

public interface Atmosphere extends ThermicObject {

	public void setBaseTemperature(float temperature);
	public float getBaseTemperature();
	
}
