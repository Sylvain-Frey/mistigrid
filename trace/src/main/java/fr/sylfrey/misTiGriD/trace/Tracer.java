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
package fr.sylfrey.misTiGriD.trace;

/**
 * Simple tracer service definition. Log users should not worry about
 * providing time information: the logger puts time-stamps by itself. 
 * @author syl
 *
 */
public interface Tracer {
	
	/**
	 * These methods must be called first for initialising logging.
	 * @param name
	 */
	public void createValueLog(String name);
	public void createMessageLog(String topic);
	
	/**
	 * Trace a particular value computed in the simulation:
	 * temperature, electricity consumption...
	 * @param name: identifying the value type, must have been
	 * declared via the createValueLog method.
	 * @param value: itself
	 */
	public void logValue(String name, float value);
	
	/**
	 * Trace a message published on a topic by agents in the simulation.
	 * @param topic: on which the message was published , must have been
	 * declared via the createMessageLog method.
	 * @param message: itself
	 */
	public void logMessage(String topic, String message);
	
}
