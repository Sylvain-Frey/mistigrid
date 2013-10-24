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

import fr.sylfrey.misTiGriD.environment.Namable;

/**
 * Standard interface for any electricity prosuming appliance.
 * Examples Prosumers : storage, washing machine, solar panel, aggregator.
 * @author syl
 *
 */
public interface Prosumer extends Namable {
	
	/**
	 * @return electrical power (in Watt) prosumed by this appliance.
	 */
	public float getProsumedPower();
	
	/**
	 * When the electrical networks shuts down, 
	 * every connected prosumer endures a blackout.
	 */
	public void blackout();
		
}
