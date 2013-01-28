package fr.sylfrey.misTiGriD.management.resources.loadHierarch;

import fr.sylfrey.misTiGriD.framework.Consumer;
import fr.sylfrey.misTiGriD.management.data.LoadMessage;

public interface LoadHierarch extends Consumer<LoadMessage> {

	public void register(Consumer<LoadMessage> loadManager);
	public void unregister(Consumer<LoadMessage> loadManager);
	public void update();
	public float maxConsumptionThreshold();
	
}
