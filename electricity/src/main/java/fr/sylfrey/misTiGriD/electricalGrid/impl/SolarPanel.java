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
import fr.sylfrey.misTiGriD.environment.Time;
import fr.sylfrey.misTiGriD.environment.Updatable;

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
