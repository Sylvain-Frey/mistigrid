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
package fr.sylfrey.misTiGriD.environment;


/**
 * Interface that any dynamic component in the simulation should implement.
 * The Updatable service should be exposed so that a simulation timer
 * can detect the component and update it periodically.
 * @author syl
 *
 */
public interface Updatable {
	
	/**
	 * The period of updates requested by this component.
	 * @return period in milliseconds
	 */
	public int getPeriod(); 
	
	/**
	 * Trigger an update of this component.
	 */
	public void update();

}
