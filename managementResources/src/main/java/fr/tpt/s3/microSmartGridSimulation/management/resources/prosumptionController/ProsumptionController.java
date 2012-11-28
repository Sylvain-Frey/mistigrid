package fr.tpt.s3.microSmartGridSimulation.management.resources.prosumptionController;

import fr.tpt.s3.microSmartGridSimulation.framework.Processor;
import fr.tpt.s3.microSmartGridSimulation.framework.data.Permission;
import fr.tpt.s3.microSmartGridSimulation.framework.data.PermissionRequest;
import fr.tpt.s3.microSmartGridSimulation.management.data.ProsumptionChange;

public interface ProsumptionController 
extends Processor<PermissionRequest<ProsumptionChange>, Permission<ProsumptionChange>> {

	@Override
	public Permission<ProsumptionChange> ask(PermissionRequest<ProsumptionChange> request);
	public void update();
	
}
