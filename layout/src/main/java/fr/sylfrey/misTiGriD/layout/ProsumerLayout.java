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
package fr.sylfrey.misTiGriD.layout;

import fr.sylfrey.misTiGriD.electricalGrid.Prosumer;

/**
 * Describes a Prosumer that has a certain size and position (cf. Layout)
 * and dynamic electrical prosumption (cf. Prosumer).
 * @author syl
 *
 */
public interface ProsumerLayout extends Layout, Prosumer {

}
