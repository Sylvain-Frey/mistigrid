package fr.tpt.s3.microSmartGridSimulation.arduino;

import fr.tpt.s3.microSmartGridSimulation.electricalGrid.TunableProsumer;

public interface Fan extends TunableProsumer {

	public float getEmissionPower();
	public float getMaxEmissionPower();
	public void setEmissionPower(float power);
}
