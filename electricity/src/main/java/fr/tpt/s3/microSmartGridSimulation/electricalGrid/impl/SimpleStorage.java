package fr.tpt.s3.microSmartGridSimulation.electricalGrid.impl;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;
//import org.apache.felix.ipojo.handlers.jmx.Config;
//import org.apache.felix.ipojo.handlers.jmx.Method;

import fr.tpt.s3.microSmartGridSimulation.electricalGrid.Aggregator;
import fr.tpt.s3.microSmartGridSimulation.electricalGrid.BlackOut;
import fr.tpt.s3.microSmartGridSimulation.electricalGrid.Prosumer;
import fr.tpt.s3.microSmartGridSimulation.electricalGrid.Storage;
import fr.tpt.s3.microSmartGridSimulation.environment.Time;
import fr.tpt.s3.microSmartGridSimulation.environment.Updatable;

@Component(name="SimpleStorage")
@Provides(specifications={Storage.class,Prosumer.class,Updatable.class})
//@Config@SuppressWarnings("deprecation")
public class SimpleStorage implements Storage, Updatable {

	@Override
	public State getState() {
		return state;
	}

	@Override
//	@Method
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

			if (state==State.LOADING && load < 0.95*MAX_LOAD ||
					state==State.UNLOADING && load > 0.05*MAX_LOAD) {
				// stop before bypassing the storage limits
				goStandBy();			
			} else {
				
				long now = time.dayTime();
				load += prosumedPower*(now-lastUpdate)/1000;
				lastUpdate = now;

			}

		}

	}

//	@Method
	private void goStandBy() {
		state = State.STANDBY;
		setProsumedPower(0);
		update();
	}

//	@Method
	private void goLoading() {
		state = State.LOADING;
		setProsumedPower(MAX_POWER_IN);
		update();
	}

//	@Method
	private void goUnloading() {
		state = State.UNLOADING;
		setProsumedPower(MAX_POWER_OUT);
		update();
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
