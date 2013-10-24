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
package fr.sylfrey.misTiGriD.electricalGrid;


public interface Lamp extends TunableProsumer, OnOffProsumer {

	/**
	 * @return the electrical power currently consumed 
	 * by this Lamp, in Watts.
	 */
	public float getEmissionPower();
	
	/**
	 * @return the maximum this Lamp can consume.
	 */
	public float getMaxEmissionPower();
	
	/**
	 * @param power: how much this Lamp consumes, in Watts.
	 */
	public void setEmissionPower(float power);
	
}
