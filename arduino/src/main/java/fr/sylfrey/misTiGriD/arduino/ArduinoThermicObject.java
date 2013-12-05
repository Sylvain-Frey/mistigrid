/*******************************************************************************
 * Copyright (c) 2013 EDF. This software was developed with the 
 * collaboration of Télécom ParisTech (Dragutin Brezak, Sylvain Frey).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Dragutin Brezak, Sylvain Frey - initial API and implementation
 ******************************************************************************/
package fr.sylfrey.misTiGriD.arduino;

import fr.sylfrey.misTiGriD.temperature.ThermicObject;


public interface ArduinoThermicObject extends ThermicObject {
	
	/**
	 * ArduinoSerialReader call this as a dispatcher and sets right temperature in each room
	 * @param temperature
	 */
	public void setCurrentTemperature(float temperature);
	
}
