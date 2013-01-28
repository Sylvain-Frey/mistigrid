package fr.sylfrey.misTiGriD.layout.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

import fr.sylfrey.misTiGriD.layout.Layout;
import fr.sylfrey.misTiGriD.layout.ThermicObjectLayout;
import fr.sylfrey.misTiGriD.temperature.ThermicObject;

@Component(name="ThermicObjectLayout",immediate=true)
@Provides(specifications={ThermicObjectLayout.class,Layout.class})
public class ThermicObjectLayoutImpl implements ThermicObjectLayout {

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
	public float getCurrentTemperature() {
		return thermicObject.getCurrentTemperature();
	}

	@Override
	public String getName() {
		return thermicObject.getName();
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
	
	@Requires(id="thermicObject")
	public ThermicObject thermicObject;
	
}
