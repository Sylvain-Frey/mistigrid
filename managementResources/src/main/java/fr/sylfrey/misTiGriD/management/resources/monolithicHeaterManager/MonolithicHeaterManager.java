package fr.sylfrey.misTiGriD.management.resources.monolithicHeaterManager;

import fr.sylfrey.misTiGriD.alba.basic.roles.HeaterManager;
import fr.sylfrey.misTiGriD.framework.Consumer;
import fr.sylfrey.misTiGriD.management.data.LoadMessage;


public interface MonolithicHeaterManager extends HeaterManager, /* Collaboration */ Consumer<LoadMessage> {

	public void update();
		
}
