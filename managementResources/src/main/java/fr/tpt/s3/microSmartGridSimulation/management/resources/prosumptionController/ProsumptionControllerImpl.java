package fr.tpt.s3.microSmartGridSimulation.management.resources.prosumptionController;

import akka.actor.ActorPath;
import akka.actor.TypedActor;
import fr.tpt.s3.microSmartGridSimulation.electricalGrid.Prosumer;
import fr.tpt.s3.microSmartGridSimulation.framework.data.Permission;
import fr.tpt.s3.microSmartGridSimulation.framework.data.PermissionRequest;
import fr.tpt.s3.microSmartGridSimulation.management.data.ProsumptionChange;

public class ProsumptionControllerImpl implements ProsumptionController {
	
	public ProsumptionControllerImpl(Prosumer prosumer, float maxConsumption) {
		this.selfPath = TypedActor.context().self().path();
		this.prosumer = prosumer;
		this.maxConsumption = maxConsumption;
	}
	
	@Override
	public Permission<ProsumptionChange> ask(PermissionRequest<ProsumptionChange> request) {
		float powerDelta = request.action.newValue - request.action.oldValue;
		boolean isGranted = 
				(powerDelta < 0) ||
				(-currentAggProsumption + powerDelta ) < maxConsumption;
		Permission<ProsumptionChange> permission = new Permission<ProsumptionChange>(
				selfPath, 
				request, 
				isGranted);
		return permission;
	}
	
	public void update() {
		currentAggProsumption = prosumer.getProsumedPower();
	}
	
	public Prosumer prosumer;
	public float maxConsumption;

	private ActorPath selfPath;
	private float currentAggProsumption;
	
}
