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
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;

import fr.sylfrey.misTiGriD.electricalGrid.Aggregator;
import fr.sylfrey.misTiGriD.electricalGrid.BlackOut;
import fr.sylfrey.misTiGriD.electricalGrid.Lamp;
import fr.sylfrey.misTiGriD.electricalGrid.Prosumer;


@Component(name="Lamp",immediate=true)
@Provides(specifications={Lamp.class,Prosumer.class})
public class LampImpl implements Lamp {

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
		
	
	@Property(name="instance.name")
	public String name;

	@Property
	public float prosumedPower;
	
	@Property
	public float maxEmissionPower;	
	
	@Requires(id="aggregator")
	public Aggregator aggregator;	
	
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
	
	protected void _setProsumedPower(float power) {
		prosumedPower = power;
		if (aggregator == null) { return; }
		try {
			aggregator.updateProsumption(this, power);
		} catch (BlackOut b) {
			blackout();
		}
	}

	@Override
	public void turnOn() {
		_setProsumedPower(-maxEmissionPower);
	}

	@Override
	public void turnOff() {
		_setProsumedPower(0);
	}
	
}
