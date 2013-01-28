package fr.tpt.s3.alba.simple.implementation;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import fr.tpt.s3.alba.simple.messages.AnyLoad;
import fr.tpt.s3.alba.simple.messages.LoadPriority;
import fr.tpt.s3.alba.simple.messages.ReduceLoad;
import fr.tpt.s3.alba.simple.roles.LoadBalancer;
import fr.tpt.s3.alba.simple.roles.Prosumer;
import fr.tpt.s3.cirrus.agent.R;
import fr.tpt.s3.microSmartGridSimulation.electricalGrid.Aggregator;

public class HouseLoadManagerImpl { //implements LoadBalancer, Prosumer {

	public HouseLoadManagerImpl(Aggregator aggregator, float maxConsumption) {
		this.aggregator = aggregator;
		this.maxConsumption = maxConsumption;
	}
	

//	@Override
//	public void tell(LoadBalancingOrder order) {
//		
//	}
//
//	@Override
//	public R<Status<ProsumerStatus>> prosumerStatus() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public R<Sensor<Prosumption>> prosumptionSensor() {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
	

//	@Override
//	public void register(R<Subordinate<LoadBalancingOrder>> loadManager) {
//		managers.add((R<Prosumer>) loadManager);
//	}
//	
//	@Override
//	public void unregister(R<Prosumer> loadManager) {
//		managers.remove(loadManager);
//	}
	

	public void update() {
		
		currentAggProsumption = aggregator.getProsumedPower();
		
		if (currentAggProsumption < maxConsumption && !managers.isEmpty()) {
			
			R<Prosumer> erasedManager = managers.remove(random.nextInt(managers.size()));
			erasedManager._().tell(new ReduceLoad(selfBalancer, LoadPriority.standard, erasedManager));
			erasedManagers.add(erasedManager);
			
		} else if (currentAggProsumption > maxConsumption + 300 && !erasedManagers.isEmpty()) {
			
			R<Prosumer> unerasedManager = erasedManagers.remove(random.nextInt(erasedManagers.size()));
			unerasedManager._().tell(new AnyLoad(selfBalancer, LoadPriority.standard, unerasedManager));
			managers.add(unerasedManager);
			
		}
	}
	

	public Aggregator aggregator;
	public float baseMaxConsumption;
	public float maxConsumption;
	
	private List<R<Prosumer>> managers = new LinkedList<R<Prosumer>>();
	private List<R<Prosumer>> erasedManagers = new LinkedList<R<Prosumer>>();
	private R<LoadBalancer> selfBalancer;
	private R<Prosumer> selfProsumer;
	private float currentAggProsumption;
	private Random random = new Random();

}
