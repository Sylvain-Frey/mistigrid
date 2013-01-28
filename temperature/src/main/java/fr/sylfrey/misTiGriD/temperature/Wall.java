package fr.sylfrey.misTiGriD.temperature;

import java.util.List;

/**
 * A wall is a frontier between several neighbouring thermic objects.
 * It has a certain heat conductance that hinders thermic exchanges between neighbours.
 * @author syl
 *
 */
public interface Wall {
	
	/**
	 * @return Float between 0 and 1.
	 */
	public float getHeatConductance();
	
	/**
	 * @return all ThermicObject adjacent to this wall.
	 */
	public List<ThermicObject> getNeighbours();
	
}
