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

import akka.actor.ActorRef;

/**
 * Specific interface for remote aggregator, 
 * same functionality as Aggregator but with serialisable ActorRefs. 
 * @author syl
 *
 */
public interface RemoteAggregator extends Prosumer {
	
	public String getName();
	public void connect(ActorRef prosumer);
	public void disconnect(ActorRef prosumer);
	public void updateProsumption(ActorRef prosumer, Float prosumption) throws BlackOut;
	
	public float getAggregatedPowerConsumption();
	public float getAggregatedPowerProduction();
	public float getBill();
	
}
