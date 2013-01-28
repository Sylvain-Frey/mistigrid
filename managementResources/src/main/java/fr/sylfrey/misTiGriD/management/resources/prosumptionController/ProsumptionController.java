package fr.sylfrey.misTiGriD.management.resources.prosumptionController;

import fr.sylfrey.misTiGriD.framework.Processor;
import fr.sylfrey.misTiGriD.framework.data.Permission;
import fr.sylfrey.misTiGriD.framework.data.PermissionRequest;
import fr.sylfrey.misTiGriD.management.data.ProsumptionChange;

public interface ProsumptionController 
extends Processor<PermissionRequest<ProsumptionChange>, Permission<ProsumptionChange>> {

	@Override
	public Permission<ProsumptionChange> ask(PermissionRequest<ProsumptionChange> request);
	public void update();
	
}
