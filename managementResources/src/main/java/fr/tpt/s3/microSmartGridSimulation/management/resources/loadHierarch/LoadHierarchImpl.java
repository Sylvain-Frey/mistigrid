package fr.tpt.s3.microSmartGridSimulation.management.resources.loadHierarch;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import akka.actor.ActorPath;
import akka.actor.TypedActor;
import fr.tpt.s3.microSmartGridSimulation.electricalGrid.Aggregator;
import fr.tpt.s3.microSmartGridSimulation.framework.Consumer;
import fr.tpt.s3.microSmartGridSimulation.management.data.Load;
import fr.tpt.s3.microSmartGridSimulation.management.data.LoadMessage;

public class LoadHierarchImpl implements LoadHierarch {
	
	public LoadHierarchImpl(Aggregator aggregator, float maxConsumption) {
		this.aggregator = aggregator;
		this.maxConsumption = maxConsumption;
		this.selfPath = TypedActor.context().self().path();
	}

	@Override
	public void register(Consumer<LoadMessage> loadManager) {
		managers.add(loadManager);
		System.out.println("# registered " + loadManager.toString());
	}
	
	@Override
	public void unregister(Consumer<LoadMessage> loadManager) {
		managers.remove(loadManager);
		System.out.println("# unregistered " + loadManager.toString());
	}

	@Override
	public void update() {
		currentAggProsumption = aggregator.getProsumedPower();
		if (currentAggProsumption < maxConsumption && !managers.isEmpty()) {
			Consumer<LoadMessage> erasedManager = managers.remove(random.nextInt(managers.size()));
			erasedManager.tell(new LoadMessage(selfPath, Load.HIGH));
//			System.out.println("# told HIGH load to " + erasedManager.toString());
			erasedManagers.add(erasedManager);
		} else if (currentAggProsumption > maxConsumption + 300 && !erasedManagers.isEmpty()) {
			Consumer<LoadMessage> unerasedManager = erasedManagers.remove(random.nextInt(erasedManagers.size()));
			unerasedManager.tell(new LoadMessage(selfPath, Load.STD));
//			System.out.println("# told STD load to " + unerasedManager.toString());
			managers.add(unerasedManager);			
		}
	}
	
	@Override
	public float maxConsumptionThreshold() {
		return maxConsumption;
	}
	
	public Aggregator aggregator;
	public float maxConsumption;
	
	private List<Consumer<LoadMessage>> managers = new LinkedList<Consumer<LoadMessage>>();
	private List<Consumer<LoadMessage>> erasedManagers = new LinkedList<Consumer<LoadMessage>>();
	private ActorPath selfPath;
	private float currentAggProsumption;
	private Random random = new Random();
	
}
