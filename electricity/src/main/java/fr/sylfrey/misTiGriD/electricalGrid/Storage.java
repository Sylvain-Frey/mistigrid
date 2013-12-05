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
package fr.sylfrey.misTiGriD.electricalGrid;

/**
 * A Storage is a particular Prosumer that stores electricity 
 * it consumes while loading and returns it while unloading.
 * A Storage has a maximum load capacity, in Watt.hour. 
 * @author syl
 *
 */
public interface Storage extends TunableProsumer {

	public enum State {
		LOADING,
		UNLOADING,
		STANDBY
	}

	/**
	 * @return whether this Storage is currently loading, unloading
	 * or doing nothing (standby). The State changes over time, for instance,
	 * when a loading Storage reaches its maximum load capacity, 
	 * it goes to Standby mode.
	 */
	public State getState();
	
	/**
	 * @param state : put this Storage into Loading, Unloading or Standby mode.
	 */
	public void setState(State state);
	
	/**
	 * @return amount of electrical energy currently stored, in Watt.hour.
	 */
	public float getLoad();
	
	/**
	 * @return maximum amount of electrical energy this Storage can load, 
	 * in Watt.hour.
	 */
	public float getLoadCapacity();
	
}
