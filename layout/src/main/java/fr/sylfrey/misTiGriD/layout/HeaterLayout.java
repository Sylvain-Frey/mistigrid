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
package fr.sylfrey.misTiGriD.layout;

import fr.sylfrey.misTiGriD.appliances.Heater;


/**
 * Describes a Heater that has a certain size and position (cf. Layout)
 * a temperature (cf. ThermicObjectLayout) and a power parameter (cf. Heater).
 * @author syl
 */
public interface HeaterLayout extends ThermicObjectLayout, Heater {

}
