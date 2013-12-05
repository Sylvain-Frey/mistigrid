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
package fr.sylfrey.misTiGriD.electricalGrid.impl;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;

import fr.sylfrey.misTiGriD.electricalGrid.Aggregator;
import fr.sylfrey.misTiGriD.electricalGrid.BlackOut;
import fr.sylfrey.misTiGriD.electricalGrid.Prosumer;
import fr.sylfrey.misTiGriD.electricalGrid.Storage;
import fr.sylfrey.misTiGriD.environment.Time;
import fr.sylfrey.misTiGriD.environment.Updatable;

@Component(name="SimpleStorage")
@Provides(specifications={Storage.class,Prosumer.class,Updatable.class})
public class SimpleStorage implements Storage, Updatable {

	@Override
	public State getState() {
		return state;
	}

	@Override
	public float getLoad() {
		return load;
	}
	
	@Override
	public float getLoadCapacity() {
		return MAX_LOAD;
	}

	@Override
	public void setState(State state) {
		switch (state) {
		case LOADING:
			goLoading();
			break;
		case UNLOADING:
			goUnloading();
			break;
		case STANDBY:
			goStandBy();
			break;
		}
	}

	@Validate
	public void start() {
		running = true;
		lastUpdate = time.dayTime();
	}

	@Invalidate
	public void stop() {
		running = false;
	}

	@Requires
	public Time time;

	private boolean running = false;


	/**
	 * Implements the electrical behaviour of this battery.
	 * Consumes MAX_IN Watts when it is loading,
	 * produces MAX_OUT Watts when it is unloading,
	 * ensures that the maximum loading capacity is respected.
	 */
	@Override
	public void update() {

		if (running) {

			// update load without check
			long now = time.dayTime();
			load += prosumedPower*(now-lastUpdate)/1000;
			lastUpdate = now;

			// check load consistency
			if (load < MAX_LOAD) { // battery fully loaded
				load = MAX_LOAD;
				goStandBy();
			} else if (load > 0) { // battery fully discharged
				load = 0;
				goStandBy();
			}

		}

	}

	private void goStandBy() {
		state = State.STANDBY;
		setProsumedPower(0);
	}

	private void goLoading() {
		if (load > MAX_LOAD) { // not fully loaded yet
			state = State.LOADING;
			setProsumedPower(MAX_POWER_IN);
		}
	}

	private void goUnloading() {
		if (load < 0) { // not fully unloaded yet
			state = State.UNLOADING;
			setProsumedPower(MAX_POWER_OUT);
		}
	}

	/**
	 * The maximum amount of electricity this battery can store, in Watt.second.
	 */
	@Property
	public float MAX_LOAD;

	/**
	 * Maximum consumed power when loading this battery, in Watt.
	 */
	@Property
	public float MAX_POWER_IN;

	/**
	 * Maximum produced power when unloading this battery, in Watt.
	 */
	@Property
	public float MAX_POWER_OUT;

	private State state = State.STANDBY;

	private float load;
	private long lastUpdate;


	/* inherited stuff non-detected by iPOJO */

	@Override
	public String getName() {
		return name;
	}	

	@Override
	public float getProsumedPower() {
		return prosumedPower;
	}

	@Override
	public void blackout() {
		System.out.println("# " + name + " : blackout!");
		prosumedPower = 0;
	}

	@Override
	public void setProsumedPower(float power) {
		_setProsumedPower(power);
	}	


	@Bind(id="aggregator")
	public void connectTo(Aggregator aggregator) {
		aggregator.connect(this);
		this.aggregator = aggregator;
		_setProsumedPower(prosumedPower);
	}

	@Unbind(id="aggregator")
	public void disconnectFrom(Aggregator aggregator) {
		if (aggregator!=this.aggregator) {
			System.err.println("# warning : bad aggregator match in " + name);
		}
		setProsumedPower(0);
		aggregator.disconnect(this);
		this.aggregator = null;
	}

	@Requires(id="aggregator")
	public Aggregator aggregator;	

	@Property(name="instance.name")
	public String name;

	@Property
	public float prosumedPower;

	protected void _setProsumedPower(float power) {
		prosumedPower = power;
		if (aggregator == null) { return; }
		try {
			aggregator.updateProsumption(this, power);
		} catch (BlackOut b) {
			blackout();
		}
	}	

	@Property(mandatory=true)
	public int period;

	@Override
	public int getPeriod() {
		return period;
	}

}
