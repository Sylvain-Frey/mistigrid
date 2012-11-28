package fr.tpt.s3.microSmartGridSimulation.management.resources.loadHierarch;

import fr.tpt.s3.microSmartGridSimulation.framework.Consumer;
import fr.tpt.s3.microSmartGridSimulation.management.data.LoadMessage;

public interface LoadHierarch {

	public void register(Consumer<LoadMessage> loadManager);
	public void unregister(Consumer<LoadMessage> loadManager);
	public void update();
	public float maxConsumptionThreshold();
	
}
