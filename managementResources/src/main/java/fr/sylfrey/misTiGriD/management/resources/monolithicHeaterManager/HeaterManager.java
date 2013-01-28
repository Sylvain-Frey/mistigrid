package fr.sylfrey.misTiGriD.management.resources.monolithicHeaterManager;

public interface HeaterManager {

	public float getRequiredTemperature();
	public void setRequiredTemperature(float requiredTemperature);

	public boolean isEconomizing();

}