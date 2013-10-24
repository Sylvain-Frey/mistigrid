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
package fr.sylfrey.misTiGriD.appliances;

import fr.sylfrey.misTiGriD.electricalGrid.TunableProsumer;
import fr.sylfrey.misTiGriD.temperature.ThermicObject;
import fr.sylfrey.misTiGriD.temperature.Wall;

/**
 * Describes a heater, that is, a ThermicObject which temperature is determined
 * by tuning its electrical consumption (cf. TunableProsumer) 
 * and that influences its thermic neighbours (cf. Wall).
 * @author syl
 *
 */
public interface Heater extends Wall, ThermicObject, TunableProsumer {

	/**
	 * @return the electrical power currently consumed 
	 * by this Heater, in Watts. How this power is converted 
	 * into temperature is a matter of implementation and efficiency.
	 */
	public float getEmissionPower();
	
	/**
	 * @return the maximum this Heater can consume (and therefore heat).
	 */
	public float getMaxEmissionPower();
	
	/**
	 * @param power: how much this Heater consumes, in Watts.
	 */
	public void setEmissionPower(float power);
	
}
