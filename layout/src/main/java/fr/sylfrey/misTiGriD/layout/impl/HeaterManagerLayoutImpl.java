package fr.sylfrey.misTiGriD.layout.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

import akka.actor.ActorRef;
import fr.sylfrey.misTiGriD.alba.basic.agents.AlbaHeaterManager;
import fr.sylfrey.misTiGriD.alba.basic.messages.LoadBalancingOrder;
import fr.sylfrey.misTiGriD.alba.basic.messages.LoadBalancingOrderResponse;
import fr.sylfrey.misTiGriD.alba.basic.messages.ProsumerStatus;
import fr.sylfrey.misTiGriD.alba.basic.messages.Prosumption;
import fr.sylfrey.misTiGriD.layout.HeaterManagerLayout;
import fr.sylfrey.misTiGriD.layout.Layout;

@Component(name="HeaterManagerLayout",immediate=true)
@Provides(specifications={HeaterManagerLayout.class,Layout.class})
public class HeaterManagerLayoutImpl implements HeaterManagerLayout {

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
	public float getRequiredTemperature() {
		return manager.getRequiredTemperature();
	}

	@Override
	public void setRequiredTemperature(float temperature) {
		manager.setRequiredTemperature(temperature);
	}
	
	@Override
	public boolean isEconomizing() {
		return manager.isEconomizing();
	}
	

	@Override
	public void update() {
		manager.update();
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
	public void onReceive(Object msg, ActorRef sender) {
		
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
	public AlbaHeaterManager manager;
	
}
