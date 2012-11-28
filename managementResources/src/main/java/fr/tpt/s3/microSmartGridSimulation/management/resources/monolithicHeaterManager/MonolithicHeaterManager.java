package fr.tpt.s3.microSmartGridSimulation.management.resources.monolithicHeaterManager;

import fr.tpt.s3.microSmartGridSimulation.framework.Consumer;
import fr.tpt.s3.microSmartGridSimulation.management.data.LoadMessage;


public interface MonolithicHeaterManager extends HeaterManager, /* Collaboration */ Consumer<LoadMessage> {

	public void update();
		
}
