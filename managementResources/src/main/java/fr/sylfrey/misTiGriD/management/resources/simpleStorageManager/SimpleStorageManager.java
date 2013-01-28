package fr.sylfrey.misTiGriD.management.resources.simpleStorageManager;

import fr.sylfrey.misTiGriD.electricalGrid.Storage;
import fr.sylfrey.misTiGriD.electricalGrid.Storage.State;
import fr.sylfrey.misTiGriD.framework.Consumer;
import fr.sylfrey.misTiGriD.management.data.LoadMessage;

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
