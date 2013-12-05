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

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Unbind;

import fr.sylfrey.misTiGriD.environment.Updatable;
import fr.sylfrey.misTiGriD.temperature.ThermicObject;
import fr.sylfrey.misTiGriD.temperature.Wall;

@Component(name="ThermicObject",immediate=true)
@Provides(specifications={ThermicObject.class,Updatable.class})
public class ThermicObjectImpl implements Updatable, ThermicObject {

	@Override
	public float getCurrentTemperature() {
		return temperature;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void update() {
		float incomingHeat = 0;
		for (Wall wall : walls.keySet()) {
			for (ThermicObject neighbour : wall.getNeighbours()) {
				incomingHeat += period*wall.getHeatConductance()*
						(neighbour.getCurrentTemperature() - temperature);
			}			
		}
		temperature += incomingHeat/heatCapacity;
	}
		
	@Bind(id="walls",aggregate=true,optional=true)
	public void bindWall(Wall wall) {
		walls.put(wall,wall.getNeighbours());
	}

	@Unbind(id="walls")
	public void unbindWall(Wall wall) {
		walls.remove(wall);
	}

	public ConcurrentHashMap<Wall,List<ThermicObject>> walls = new ConcurrentHashMap<Wall,List<ThermicObject>>();

	@Property(name="instance.name",mandatory=true)
	public String name;
	
	@Property(mandatory=true)
	public float temperature;
	
	/**
	 * Should be greater or equal than the number of thermic neighbours.
	 */
	@Property(mandatory=true)
	public float heatCapacity;

	@Property(mandatory=true)
	public int period;

	@Override
	public int getPeriod() {
		return period;
	}
	
}
