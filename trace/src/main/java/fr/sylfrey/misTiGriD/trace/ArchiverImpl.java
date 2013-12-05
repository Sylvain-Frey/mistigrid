/*******************************************************************************
 * Copyright (c) 2013 EDF. This software was developed with the 
 * collaboration of Télécom ParisTech (Sylvain Frey).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Sylvain Frey - initial API and implementation
 ******************************************************************************/
package fr.sylfrey.misTiGriD.trace;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;

import akka.event.EventBus;
import akka.event.EventStream;
import fr.sylfrey.misTiGriD.alba.basic.agents.HouseLoadManager;
import fr.sylfrey.misTiGriD.appliances.Heater;
import fr.sylfrey.misTiGriD.electricalGrid.Prosumer;
import fr.sylfrey.misTiGriD.environment.Time;
import fr.sylfrey.misTiGriD.environment.Updatable;
import fr.sylfrey.misTiGriD.temperature.ThermicObject;

/**
 * Collects simulation data from simulation objects,
 * and forwards it to a Tracer for logging and persistence.
 * @author syl
 *
 */
@Component(name="Archiver", immediate=true)
@Provides
public class ArchiverImpl implements Updatable, Archiver {

	@Property
	public int period = 100;

	@Requires
	public Tracer tracer;
	
	@Requires
	public Time time;

	@Bind(aggregate=true, optional=true)
	public void bindThermicObject(ThermicObject to) {
		tracer.createValueLog("temperature_" + to.getName());
		thermicObjects.add(to);
	}
	private List<ThermicObject> thermicObjects = new LinkedList<ThermicObject>();


	@Bind(aggregate=true, optional=true)
	public void bindProsumer(Prosumer prosumer) {
		tracer.createValueLog("prosumption_" + prosumer.getName());
		prosumers.add(prosumer);
	}
	private Collection<Prosumer> prosumers = new ConcurrentLinkedQueue<Prosumer>();

	@Bind(id="loadManager", optional=true)
	public void bindLoadManager(HouseLoadManager houseLoadManager) {
		this.houseLoadManager = houseLoadManager;
		tracer.createValueLog("loadManager_maxConsumptionGoal");
		tracer.createValueLog("loadManager_minConsumptionGoal");
	}

	@Unbind(id="loadManager", optional=true)
	public void unbindLoadManager(HouseLoadManager houseLoadManager) {
		houseLoadManager = null;
	}

	private HouseLoadManager houseLoadManager;


	private EventBus bus = new EventStream(false);


	@Override
	public int getPeriod() {
		return period;
	}

	@Override
	public void update() {
		
		try {			
			// ConcurrentModificationException happen 
			// when new POJOs are bound to the ArchiverImpl:
			// discard them.
			
			for (ThermicObject to : thermicObjects) {
				tracer.logValue("temperature_" + to.getName(), to.getCurrentTemperature());
				bus.publish(new ArchiverEvent<Float>("temperature_" + to.getName(), to.getCurrentTemperature()));
			}
			
			for (Prosumer prosumer : prosumers) {
				tracer.logValue("prosumption_" + prosumer.getName(), prosumer.getProsumedPower());
				bus.publish(new ArchiverEvent<Float>("prosumption_" + prosumer.getName(), prosumer.getProsumedPower()));
				if (prosumer instanceof Heater) {
					bus.publish(new ArchiverEvent<Float>(
							"temperature_" + prosumer.getName(), 
							((Heater) prosumer).getCurrentTemperature()));
				}
			}
			
		} catch (ConcurrentModificationException e) {
			// losing logs is no big deal, is it?
		}
		
		if (houseLoadManager != null) {
			tracer.logValue("loadManager_maxConsumptionGoal", houseLoadManager.maxConsumptionThreshold());
			bus.publish(new ArchiverEvent<Float>("loadManager_maxConsumptionGoal", houseLoadManager.maxConsumptionThreshold()));
			tracer.logValue("loadManager_minConsumptionGoal", houseLoadManager.minConsumptionThreshold());
			bus.publish(new ArchiverEvent<Float>("loadManager_minConsumptionGoal", houseLoadManager.minConsumptionThreshold()));
		}
		
		bus.publish(new ArchiverEvent<Long>("time", time.dayTime()));
		bus.publish(new ArchiverEvent<Long>("dayLength", time.dayLength()));
		
	}

	@Override
	public EventBus bus() {
		return bus;
	}

}
