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
package fr.sylfrey.misTiGriD.alba.basic.roles;

import akka.actor.ActorRef;
//import fr.sylfrey.misTiGriD.alba.basic.messages.ProsumerStatus;

public interface LoadManager {

	public void register(ActorRef prosumer);
	public void unregister(ActorRef prosumer);

	public float maxConsumptionThreshold();
	public void setMaximumProsumption(float threshold);
	public float minConsumptionThreshold();
//	public void setStatus(ProsumerStatus status);

}
