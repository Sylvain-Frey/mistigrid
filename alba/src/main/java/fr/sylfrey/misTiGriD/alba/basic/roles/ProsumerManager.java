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
package fr.sylfrey.misTiGriD.alba.basic.roles;

import fr.sylfrey.misTiGriD.alba.basic.messages.LoadBalancingOrder;
import fr.sylfrey.misTiGriD.alba.basic.messages.LoadBalancingOrderResponse;
import fr.sylfrey.misTiGriD.alba.basic.messages.ProsumerStatus;
import fr.sylfrey.misTiGriD.alba.basic.messages.Prosumption;

public interface ProsumerManager {

	public Prosumption getProsumption(); 
	public ProsumerStatus getStatus(); 
	public void setStatus(ProsumerStatus status);
	public LoadBalancingOrderResponse tell(LoadBalancingOrder order); 
	  
}
