package fr.tpt.s3.microSmartGridSimulation.temperature.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Unbind;

import fr.tpt.s3.microSmartGridSimulation.temperature.Opening;
import fr.tpt.s3.microSmartGridSimulation.temperature.ThermicObject;
import fr.tpt.s3.microSmartGridSimulation.temperature.Wall;

/**
 * A physical wall that separates rooms in a house, or a room and the external atmosphere.
 * This wall features an opening by default, for instance a door or window;
 * the opening state (open/closed) changes the wall's heat conductance.
 * @author syl
 *
 */
@Component(name="Wall",immediate=true)
@Provides(specifications={Wall.class,Opening.class})
public class WallImpl implements Wall, Opening {

	/**
	 * Open this wall's door or window.
	 */
	@Override
	public void open() {
		isOpen = true;
	}

	/**
	 * Close this wall's door or window.
	 */
	@Override
	public void close() {
		isOpen = false;
	}

	/**
	 * Is the door/window open?
	 */
	@Override
	public boolean isOpen() {
		return isOpen;
	}

	/**
	 * Is the door/window closed?
	 */
	@Override
	public boolean isClosed() {
		return !isOpen;
	}
	
	@Override
	public float getHeatConductance() {
		return isOpen ? openHeatConductance : surfacicHeatConductance*size;
	}
	
	@Override
	public List<ThermicObject> getNeighbours() {
		return thermicNeighbours;
	}
	
	@Property(name="instance.name")
	public String name;
	
	/**
	 * The higher this parameter, the most this wall will allow heat transfers.
	 */
	@Property
	public float surfacicHeatConductance;
	
	/**
	 * Initial state of the opening.
	 */
	@Property
	public boolean isOpen;
	
	/**
	 * Heat conductance of this wall in case the opening is open.
	 * Should much higher than surfacicHeatConductance*size.
	 */
	@Property
	public float openHeatConductance;
	
	/**
	 * Surface of this wall, the larger it is, the bigger heat transfers it allows.
	 */
	@Property
	public float size;

	@Bind(id="thermicNeighbours",aggregate=true)
	public void bindNeighbour(ThermicObject neighbour) {
		thermicNeighbours.add(neighbour);
	}

	@Unbind(id="thermicNeighbours")
	public void unbindThermicNeighbour(ThermicObject neighbour) {
		thermicNeighbours.remove(neighbour);
	}
	
	public List<ThermicObject> thermicNeighbours = new ArrayList<ThermicObject>();
	
}
