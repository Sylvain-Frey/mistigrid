package fr.tpt.s3.microSmartGridSimulation.temperature.impl;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Unbind;

import fr.tpt.s3.microSmartGridSimulation.environment.Updatable;
import fr.tpt.s3.microSmartGridSimulation.temperature.ThermicObject;
import fr.tpt.s3.microSmartGridSimulation.temperature.Wall;

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
