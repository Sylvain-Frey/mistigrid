package fr.tpt.s3.microSmartGridSimulation.management.resources.simpleStorageManager;

import fr.tpt.s3.microSmartGridSimulation.electricalGrid.Storage;
import fr.tpt.s3.microSmartGridSimulation.electricalGrid.Storage.State;
import fr.tpt.s3.microSmartGridSimulation.framework.Consumer;
import fr.tpt.s3.microSmartGridSimulation.management.data.LoadMessage;

public class SimpleStorageManager implements Consumer<LoadMessage> {

	public SimpleStorageManager(Storage storage) {
		super();
		this.storage = storage;
	}

	@Override
	public void tell(LoadMessage message) {
		switch (message.load) {
		case LOW:
			storage.setState(State.LOADING);
			break;
		case STD:
			storage.setState(State.STANDBY);
			break;
		case HIGH:
			storage.setState(State.UNLOADING);
			break;		
		}
	}
	
	public Storage storage;

}
