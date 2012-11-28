package fr.tpt.s3.microSmartGridSimulation.layout.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

import fr.tpt.s3.microSmartGridSimulation.layout.AtmosphereLayout;
import fr.tpt.s3.microSmartGridSimulation.layout.Layout;
import fr.tpt.s3.microSmartGridSimulation.temperature.Atmosphere;

@Component(name="AtmosphereLayout",immediate=true)
@Provides(specifications={AtmosphereLayout.class,Layout.class})
public class AtmosphereLayoutImpl implements AtmosphereLayout {

	@Override
	public float getBaseTemperature() {
		return atmosphere.getBaseTemperature();
	}
	
	@Override
	public void setBaseTemperature(float temperature) {
		atmosphere.setBaseTemperature(temperature);
	}

	@Override
	public float getCurrentTemperature() {
		return atmosphere.getCurrentTemperature();
	}

	@Override
	public String getName() {
		return atmosphere.getName();
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
	
	@Requires(id="atmosphere")
	public Atmosphere atmosphere;

}
