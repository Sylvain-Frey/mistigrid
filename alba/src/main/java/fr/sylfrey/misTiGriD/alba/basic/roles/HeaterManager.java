package fr.sylfrey.misTiGriD.alba.basic.roles;

public interface HeaterManager {

	public float getRequiredTemperature();
	public void setRequiredTemperature(float requiredTemperature);

	public boolean isEconomizing();

}