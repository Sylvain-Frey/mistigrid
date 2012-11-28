package fr.tpt.s3.microSmartGridSimulation.electricalGrid.impl;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;

import fr.tpt.s3.microSmartGridSimulation.electricalGrid.Aggregator;
import fr.tpt.s3.microSmartGridSimulation.electricalGrid.BlackOut;
import fr.tpt.s3.microSmartGridSimulation.electricalGrid.Prosumer;
import fr.tpt.s3.microSmartGridSimulation.environment.Time;
import fr.tpt.s3.microSmartGridSimulation.environment.Updatable;

@Component(name="SolarPanel")
@Provides(specifications={Prosumer.class,Updatable.class})
public class SolarPanel implements Prosumer, Updatable {

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
		System.out.println("# solar panel " + name + " : blackout!");
	}
	
	@Override
	public void update() {
		if (running) {
			prosumedPower = (float) Math.max(0,
					maximalProducedPower * (-1) 
					* Math.cos(2* Math.PI * time.dayTime()/time.dayLength()));
			try {
				aggregator.updateProsumption(this, prosumedPower);
			} catch (BlackOut b) {
				b.printStackTrace();
			}
		}
	}
	
	@Bind(id="aggregator")
	public void connectTo(Aggregator aggregator) {
		aggregator.connect(this);
		try {
			aggregator.updateProsumption(this, prosumedPower);
			this.aggregator = aggregator;
		} catch (BlackOut b) {
			b.printStackTrace();
		}
	}

	@Unbind(id="aggregator")
	public void disconnectFrom(Aggregator aggregator) {
		if (aggregator!=this.aggregator) {
			System.err.println("# warning : bad aggregator match in " + name);
		}
		try {
			aggregator.updateProsumption(this, 0);
			aggregator.disconnect(this);
			this.aggregator = null;
		} catch (BlackOut b) {
			b.printStackTrace();
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
	
	@Property(mandatory=true)
	public int period;

	@Override
	public int getPeriod() {
		return period;
	}
	
	@Requires
	private Aggregator aggregator;
	
	@Requires
	private Time time;
	
	@Property(name="instance.name")
	protected String name;
	
	private float prosumedPower;
	
	@Property
	private float maximalProducedPower;
	
	private boolean running;

}
