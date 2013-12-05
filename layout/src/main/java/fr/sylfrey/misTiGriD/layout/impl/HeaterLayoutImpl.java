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
package fr.sylfrey.misTiGriD.layout.impl;

import java.util.List;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

import fr.sylfrey.misTiGriD.appliances.Heater;
import fr.sylfrey.misTiGriD.layout.HeaterLayout;
import fr.sylfrey.misTiGriD.layout.Layout;
import fr.sylfrey.misTiGriD.temperature.ThermicObject;

@Component(name="HeaterLayout",immediate=true)
@Provides(specifications={HeaterLayout.class,Layout.class})
public class HeaterLayoutImpl implements HeaterLayout {

	@Override 
	public String name() {
		return name;
	}

	@Override
	public int x() {
		return x;
	}

	@Override
	public int y() {
		return y;
	}

	@Override
	public int width() {
		return width;
	}

	@Override
	public int height() {
		return height;
	}

	@Override
	public int layer() {
		return layer;
	}
	
	@Override
	public float getCurrentTemperature() {
		return heater.getCurrentTemperature();
	}

	@Override
	public String getName() {
		return heater.getName();
	}

	@Override
	public float getEmissionPower() {
		return heater.getEmissionPower();
	}

	@Override
	public float getMaxEmissionPower() {
		return heater.getMaxEmissionPower();
	}

	@Override
	public void setEmissionPower(float power) {
		heater.setEmissionPower(power);
	}

	@Override
	public float getHeatConductance() {
		return heater.getHeatConductance();
	}

	@Override
	public List<ThermicObject> getNeighbours() {
		return heater.getNeighbours();
	}

	@Override
	public void setProsumedPower(float power) {
		heater.setProsumedPower(power);
	}

	@Override
	public float getProsumedPower() {
		return heater.getProsumedPower();
	}

	@Override
	public void blackout() {
		heater.blackout();
	}
	

	@Property(name="layout.name")
	public String name;
	
	@Property
	public int x;

	@Property
	public int y;

	@Property
	public int width;

	@Property
	public int height;

	@Property
	public int layer;
	
	@Requires(id="heater")
	public Heater heater;

}
