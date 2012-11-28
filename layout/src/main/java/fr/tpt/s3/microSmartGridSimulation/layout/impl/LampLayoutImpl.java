package fr.tpt.s3.microSmartGridSimulation.layout.impl;

import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

import fr.tpt.s3.microSmartGridSimulation.electricalGrid.Lamp;
import fr.tpt.s3.microSmartGridSimulation.layout.LampLayout;
import fr.tpt.s3.microSmartGridSimulation.layout.Layout;

@org.apache.felix.ipojo.annotations.Component(name="LampLayout")
@Provides(specifications={LampLayout.class, Layout.class})
public class LampLayoutImpl implements LampLayout {
	
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
	public float getEmissionPower() {
		return lamp.getEmissionPower();
	}

	@Override
	public float getMaxEmissionPower() {
		return lamp.getMaxEmissionPower();
	}

	@Override
	public void setEmissionPower(float power) {
		lamp.setEmissionPower(power);
	}

	@Override
	public void turnOn() {
		lamp.turnOn();
	}

	@Override
	public void turnOff() {
		lamp.turnOff();
	}

	@Override
	public void setProsumedPower(float power) {
		lamp.setProsumedPower(power);
	}

	@Override
	public float getProsumedPower() {
		return lamp.getProsumedPower();
	}

	@Override
	public void blackout() {
		lamp.blackout();
	}

	@Override
	public String getName() {
		return lamp.getName();
	}
	
	@Requires(id="lamp")
	public Lamp lamp; 

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
	
}