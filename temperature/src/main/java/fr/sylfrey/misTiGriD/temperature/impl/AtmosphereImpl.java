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
package fr.sylfrey.misTiGriD.temperature.impl;

import java.util.Random;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

import fr.sylfrey.misTiGriD.environment.Time;
import fr.sylfrey.misTiGriD.environment.Updatable;
import fr.sylfrey.misTiGriD.temperature.Atmosphere;
import fr.sylfrey.misTiGriD.temperature.ThermicObject;

@Component(name="Atmosphere",immediate=true)
@Provides(specifications={ThermicObject.class,Updatable.class, Atmosphere.class})
public class AtmosphereImpl implements Updatable, ThermicObject, Atmosphere {

	@Override
	public float getCurrentTemperature() {
		return randomisedTemperature;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setBaseTemperature(float temperature) {
		this.temperature= temperature;		
	}
	
	public float getBaseTemperature() {
		return temperature;
	}

	@Override
	public void update() {
		if (!isManual) {
			randomisedTemperature = 
				(float) ((-1) * (maxTemperature - minTemperature) / 2 
				* Math.cos(2*Math.PI*time.dayTime()/time.dayLength())
				+ (maxTemperature + minTemperature) / 2);
		}else {
			// introducing random +/- 1°C
			float delta = 0;
			int dice = random.nextInt(4);
			if (dice == 0) delta = -1;
			else if (dice == 3) delta = 1;
			randomisedTemperature = temperature + delta;
		}
	}
	
	@Requires
	public Time time;

	@Property(name="instance.name",mandatory=true)
	public String name;
	
	@Property(mandatory=true)
	public boolean isManual;
	
	@Property(mandatory=true)
	public float temperature;
	
	@Property(mandatory=true)
	public float minTemperature;
	
	@Property(mandatory=true)
	public float maxTemperature;
	
	@Property(mandatory=true)
	public int period;

	@Override
	public int getPeriod() {
		return period;
	}
	
	private Random random = new Random();
	private float randomisedTemperature = temperature;

}
