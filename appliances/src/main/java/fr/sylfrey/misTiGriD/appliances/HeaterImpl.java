package fr.sylfrey.misTiGriD.appliances;

import java.util.ArrayList;
import java.util.List;

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
import fr.sylfrey.misTiGriD.environment.Updatable;
import fr.sylfrey.misTiGriD.temperature.ThermicObject;
import fr.sylfrey.misTiGriD.temperature.Wall;

@Component(name="Heater",immediate=true)
@Provides(specifications={Heater.class,Prosumer.class,Wall.class, Updatable.class, ThermicObject.class})
public class HeaterImpl implements Heater, Updatable {

	/**
	 * !!
	 * Added updatable behaviour but period, heatcapacity properties
	 * are not configurable -> TODO = change deploy bundle, configs, etc. 
	 * !!
	 */

	private final int period = 1000;
	private float temperature;
	private final float heatCapacity = 100;
	
	@Override
	public void update() {
		if (getEmissionPower()!=0) { // heater is on
			temperature = room.getCurrentTemperature() - getProsumedPower()*efficiency;
		} else { /// heater is off, simulate decay
			float heatFlux = (room.getCurrentTemperature() - temperature)*
					period*heatConductance;
			temperature += heatFlux/heatCapacity;			
		}
		// ensure temperature is never lower than room's
		// htf does that happens??? :/ -> long time between updates??
		temperature = Math.max(temperature, room.getCurrentTemperature());
	}


	@Override
	public int getPeriod() {
		return period;		
	}
	
	@Override
	public float getEmissionPower() {
		return -getProsumedPower();
	}

	@Override
	public float getMaxEmissionPower() {
		return maxEmissionPower;
	}

	@Override
	public void setEmissionPower(float power) {
		float effectivePower = 0;
		effectivePower = Math.max(0,power);
		effectivePower = Math.min(maxEmissionPower,power);
		setProsumedPower(-effectivePower);
	}	
	
	@Override
	public float getCurrentTemperature() {
		//at least the temperature of the enclosing room.
		//return Math.max(-getProsumedPower()*efficiency, room.getCurrentTemperature());
		return temperature;
	}

	@Override
	public float getHeatConductance() {
		return heatConductance;
	}

	@Override
	public List<ThermicObject> getNeighbours() {
		return thermicNeighbours;
	}
	
	
	@Property(name="instance.name")
	public String name;

	@Property
	public float prosumedPower;
	
	@Property
	public float heatConductance;
	
	@Property
	public float efficiency;	

	@Property
	public float maxEmissionPower;	
	
	@Requires(id="aggregator")
	public Aggregator aggregator;	
	
	@Requires(id="room")
	public ThermicObject room;
	
	public List<ThermicObject> thermicNeighbours = new ArrayList<ThermicObject>();
	
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
	
	@Validate
	public void start() {
		thermicNeighbours.add(this);
	}
	
	@Invalidate
	public void stop() {}
		
	protected void _setProsumedPower(float power) {
		prosumedPower = power;
		if (aggregator == null) { return; }
		try {
			aggregator.updateProsumption(this, power);
		} catch (BlackOut b) {
			blackout();
		}
	}
	
}
