package fr.sylfrey.misTiGriD.arduino;

import fr.sylfrey.misTiGriD.electricalGrid.TunableProsumer;

public interface Fan extends TunableProsumer {

	public float getEmissionPower();
	public float getMaxEmissionPower();
	public void setEmissionPower(float power);
}
