package fr.sylfrey.misTiGriD.layout.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

import fr.sylfrey.misTiGriD.layout.AtmosphereLayout;
import fr.sylfrey.misTiGriD.layout.Layout;
import fr.sylfrey.misTiGriD.temperature.Atmosphere;

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
	
	@Requires(id="atmosphere")
	public Atmosphere atmosphere;

}
