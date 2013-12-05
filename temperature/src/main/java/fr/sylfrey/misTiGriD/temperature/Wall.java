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
