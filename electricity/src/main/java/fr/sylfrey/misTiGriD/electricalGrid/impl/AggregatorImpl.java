/*******************************************************************************
 * Copyright (c) 2013 Sylvain Frey.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Sylvain Frey - initial API and implementation
 ******************************************************************************/
package fr.sylfrey.misTiGriD.electricalGrid.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import akka.japi.Creator;
import fr.sylfrey.misTiGriD.electricalGrid.Aggregator;
import fr.sylfrey.misTiGriD.electricalGrid.BlackOut;
import fr.sylfrey.misTiGriD.electricalGrid.Prosumer;
import fr.sylfrey.misTiGriD.electricalGrid.RemoteAggregator;
import fr.sylfrey.misTiGriD.wrappers.ActorSystemProvider;

@Component(name="Aggregator")
@Provides(specifications={Aggregator.class,Prosumer.class})
public class AggregatorImpl implements Aggregator, Prosumer {

	@Validate
	public void start() { 
		
		// publish yourself as a remote aggregator via Akka proxy
		RemoteAggregator proxy = TypedActor.get(actorSystem).typedActorOf(
				new TypedProps<RemoteAdapterImpl>(
						RemoteAggregator.class, 
						new Creator<RemoteAdapterImpl>() {
							public RemoteAdapterImpl create() {
								return new RemoteAdapterImpl(AggregatorImpl.this, actorSystem);
							}
						}),
						actorPath);		
		self = TypedActor.get(actorSystem).getActorRefFor(proxy);
		System.out.println("# published remote aggregator at " + self);
	
		
		if (hasRemoteParent && remoteParentURL!=null) {
						
			// connect to remote parent aggregator			
			try {
				
				System.out.println("# connecting to " + remoteParentURL + "...");	
				ActorRef parentRef = actorSystem.actorFor(remoteParentURL);
				parent = TypedActor.get(actorSystem).typedActorOf(
							new TypedProps<RemoteAggregator>(RemoteAggregator.class),								
							parentRef);
				parent.connect(self);
				System.out.println("# succesfully connected to " + parent.getName() + " aka "+ parent.toString());
								
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} 	

	}
	
	@Invalidate
	public void stop() {
		if (parent!=null) {
			parent.disconnect(self);
		}
		TypedActor.get(actorSystem).poisonPill(self);
	}

	
	@Property(name="instance.name")
	public String name;
	
	@Property
	public String actorPath;

	@Property
	public boolean hasRemoteParent;
	
	@Property
	public String remoteParentURL;
	
	@Bind
	public void bindActorSystem(ActorSystemProvider provider) {
		actorSystem = provider.getSystem();
	}

	public ActorSystem actorSystem;
	private ActorRef self;
	
	private RemoteAggregator parent;

	private Map<Prosumer,Float> prosumptions = new ConcurrentHashMap<Prosumer,Float>();
	private Map<Prosumer,Float> consumptions = new ConcurrentHashMap<Prosumer,Float>();
	private Map<Prosumer,Float> productions = new ConcurrentHashMap<Prosumer,Float>();

	private float aggregatedPowerConsumption = 0;
	private float aggregatedPowerProduction = 0;

	private float price = 0f;
	private float bill = 0f;
	
	private float prosumedPower;
		
	
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


}
