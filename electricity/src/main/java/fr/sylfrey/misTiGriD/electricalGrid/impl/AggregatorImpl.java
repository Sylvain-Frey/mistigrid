package fr.sylfrey.misTiGriD.electricalGrid.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import akka.actor.ActorRef;
import akka.actor.TypedActor;
import fr.sylfrey.misTiGriD.electricalGrid.Aggregator;
import fr.sylfrey.misTiGriD.electricalGrid.BlackOut;
import fr.sylfrey.misTiGriD.electricalGrid.Prosumer;
import fr.sylfrey.misTiGriD.electricalGrid.RemoteAggregator;

public class AggregatorImpl implements Aggregator, Prosumer {

	public AggregatorImpl(RemoteAggregator parent, String name) {
		this.parent = parent;
		this.name = name;
		self = TypedActor.context().self();
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void connect(Prosumer prosumer) {
		prosumptions.put(prosumer,0f);
	}

	@Override
	public void disconnect(Prosumer prosumer) {
		prosumptions.remove(prosumer);
	}

	@Override
	public void updateProsumption(Prosumer prosumer, float prosumption) {

		if (prosumptions.get(prosumer)==null) {
			System.err.println("# warning, non declared prosumer in Aggregator " + name);
			return;
		}

		// mind the old value
		float oldValue = prosumptions.get(prosumer);
		prosumedPower += prosumption - oldValue;
		prosumptions.put(prosumer, prosumption);

		if (prosumption>0) {
			productions.put(prosumer, prosumption);
			consumptions.remove(prosumer);
			aggregatedPowerProduction += prosumption;
		} else {
			consumptions.put(prosumer, prosumption);
			productions.remove(prosumer);
			aggregatedPowerConsumption -= prosumption;
		}

		if (oldValue>0) {
			aggregatedPowerProduction -= oldValue;
		} else {
			aggregatedPowerConsumption += oldValue;
		}

		bill += prosumption*price;
		
		if (parent!=null) {
			try {
				parent.updateProsumption(self,prosumedPower);
			} catch (BlackOut e) {
				blackout();
			}
		}
		
//		System.out.println("# " + name + " updated: " + prosumer.toString() + " ~ " + prosumption);

	}

	@Override
	public float getProsumedPower() {
		return prosumedPower;
	}

	@Override
	public void blackout() {
		prosumedPower = 0;
		for (Prosumer p : prosumptions.keySet()) {
			p.blackout();
			prosumptions.put(p, 0f);
		}
		for (Prosumer p : consumptions.keySet()) {
			consumptions.put(p, 0f);
		}
		for (Prosumer p : productions.keySet()) {
			productions.put(p, 0f);
		}
		aggregatedPowerConsumption = 0f;
		aggregatedPowerProduction = 0f;
	}

	@Override
	public float getBill() {
		return bill;
	}

	@Override
	public float getAggregatedPowerConsumption() {
		return aggregatedPowerConsumption;
	}

	@Override
	public float getAggregatedPowerProduction() {
		return aggregatedPowerProduction;
	}


	public float getPrice() {
		return price;
	}
	
	public void setPrice(float price) {
		this.price = price;
	}
	
	public String name;
	public RemoteAggregator parent;

	private Map<Prosumer,Float> prosumptions = new ConcurrentHashMap<Prosumer,Float>();
	private Map<Prosumer,Float> consumptions = new ConcurrentHashMap<Prosumer,Float>();
	private Map<Prosumer,Float> productions = new ConcurrentHashMap<Prosumer,Float>();

	private float aggregatedPowerConsumption = 0;
	private float aggregatedPowerProduction = 0;

	private float price = 0f;
	private float bill = 0f;
	
	private float prosumedPower;
	
	private ActorRef self;
	
}
