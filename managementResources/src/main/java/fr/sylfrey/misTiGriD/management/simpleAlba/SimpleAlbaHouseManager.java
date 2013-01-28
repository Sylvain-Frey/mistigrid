package fr.sylfrey.misTiGriD.management.simpleAlba;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import akka.actor.ActorPath;
import akka.actor.TypedActor;
import fr.sylfrey.misTiGriD.electricalGrid.Aggregator;
import fr.sylfrey.misTiGriD.framework.Consumer;
import fr.sylfrey.misTiGriD.management.data.Load;
import fr.sylfrey.misTiGriD.management.data.LoadMessage;
import fr.sylfrey.misTiGriD.management.resources.loadHierarch.LoadHierarch;

public class SimpleAlbaHouseManager implements LoadHierarch {

	public SimpleAlbaHouseManager() {
		
	}
	
	public SimpleAlbaHouseManager(Aggregator aggregator, float maxConsumption) {
		this.aggregator = aggregator;
		this.baseMaxConsumption = maxConsumption;
		this.maxConsumption = maxConsumption;
		this.load = Load.STD;
//		this.selfPath = TypedActor.context().self().path();
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
		selfPath = TypedActor.context().self().path();
		currentAggProsumption = aggregator.getProsumedPower();
		if (currentAggProsumption < baseMaxConsumption && !managers.isEmpty()) {
			Consumer<LoadMessage> erasedManager = managers.remove(random.nextInt(managers.size()));
			erasedManager.tell(new LoadMessage(selfPath, Load.HIGH));
//			System.out.println("# told HIGH load to " + erasedManager.toString());
			erasedManagers.add(erasedManager);
		} else if (currentAggProsumption > baseMaxConsumption + 300 && !erasedManagers.isEmpty()) {
			Consumer<LoadMessage> unerasedManager = erasedManagers.remove(random.nextInt(erasedManagers.size()));
			unerasedManager.tell(new LoadMessage(selfPath, Load.STD));
//			System.out.println("# told STD load to " + unerasedManager.toString());
			managers.add(unerasedManager);			
		}
	}
	
	@Override
	public float maxConsumptionThreshold() {
		return baseMaxConsumption;
	}
	
	@Override
	public void tell(LoadMessage msg) {
		
		if (msg.equals(Load.STD) && !load.equals(Load.STD)) {
			load = Load.STD;
			maxConsumption = baseMaxConsumption;
		} else if (msg.equals(Load.HIGH) && !load.equals(Load.HIGH)) {
			load = Load.HIGH;
			maxConsumption = baseMaxConsumption + 1000;
		}
		
	}
	
	public Aggregator aggregator;
	public float baseMaxConsumption;
	public float maxConsumption;
	public Load load;
	
	public List<Consumer<LoadMessage>> managers = new LinkedList<Consumer<LoadMessage>>();
	public List<Consumer<LoadMessage>> erasedManagers = new LinkedList<Consumer<LoadMessage>>();
	public ActorPath selfPath;
	public float currentAggProsumption;
	public Random random = new Random();

}
