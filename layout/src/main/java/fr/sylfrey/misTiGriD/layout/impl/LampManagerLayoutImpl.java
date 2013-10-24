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
package fr.sylfrey.misTiGriD.layout.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

import fr.sylfrey.misTiGriD.alba.basic.agents.LampManager;
import fr.sylfrey.misTiGriD.alba.basic.messages.LoadBalancingOrder;
import fr.sylfrey.misTiGriD.alba.basic.messages.LoadBalancingOrderResponse;
import fr.sylfrey.misTiGriD.alba.basic.messages.ProsumerStatus;
import fr.sylfrey.misTiGriD.alba.basic.messages.Prosumption;
import fr.sylfrey.misTiGriD.layout.LampManagerLayout;
import fr.sylfrey.misTiGriD.layout.Layout;

@Component(name="LampManagerLayout",immediate=true)
@Provides(specifications={LampManagerLayout.class,Layout.class})
public class LampManagerLayoutImpl implements LampManagerLayout {

	@Override 
	public String name() {
		return name;
	}

	@Override
	public int x() {
		return x;
	}

	@Override
	public int y() {
		return y;
	}

	@Override
	public int width() {
		return width;
	}

	@Override
	public int height() {
		return height;
	}

	@Override
	public int layer() {
		return layer;
	}
	
	@Override
	public boolean isEconomising() {
		return manager.isEconomising();
	}

	@Override
	public Prosumption getProsumption() {
		return manager.getProsumption();
	}

	@Override
	public ProsumerStatus getStatus() {
		return manager.getStatus();
	}
	
	@Override
	public void setStatus(ProsumerStatus status) {
		manager.setStatus(status);
	}

	@Override
	public LoadBalancingOrderResponse tell(LoadBalancingOrder order) {
		return manager.tell(order);
	}

	@Override
	public void update() {		
	} 

	@Property(name="layout.name")
	public String name;
		
	@Property
	public int x;

	@Property
	public int y;

	@Property
	public int width;

	@Property
	public int height;

	@Property
	public int layer;

	@Requires(id="manager")
	public LampManager manager;

}
