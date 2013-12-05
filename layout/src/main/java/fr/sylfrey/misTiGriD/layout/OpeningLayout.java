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

import fr.sylfrey.misTiGriD.temperature.Opening;

/**
 * Describes a Wall with a certain size and position (cf. Layout)
 * and possibly an Opening that can be opened, closed (cf. Opening).
 * @author syl
 */
public interface OpeningLayout extends Layout, Opening {

}
