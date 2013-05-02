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
import fr.sylfrey.misTiGriD.electricalGrid.TunableProsumer;
//import org.apache.felix.ipojo.handlers.jmx.Config;


/**
 * Simple prosumer with externally controlled prosumption.
 * @author syl
 */
@Component(name="SimpleTunableProsumer")
@Provides(specifications={TunableProsumer.class,Prosumer.class})
//@Config@SuppressWarnings("deprecation")
public class SimpleTunableProsumer implements TunableProsumer {

	@Validate
	public void start() {
	}
	
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
	
	@Invalidate
	public void stop() {
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
	
}
