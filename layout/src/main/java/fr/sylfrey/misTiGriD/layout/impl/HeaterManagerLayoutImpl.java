package fr.sylfrey.misTiGriD.layout.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

import fr.sylfrey.misTiGriD.layout.HeaterManagerLayout;
import fr.sylfrey.misTiGriD.layout.Layout;
import fr.sylfrey.misTiGriD.management.resources.monolithicHeaterManager.HeaterManager;

@Component(name="HeaterManagerLayout",immediate=true)
@Provides(specifications={HeaterManagerLayout.class,Layout.class})
public class HeaterManagerLayoutImpl implements HeaterManagerLayout {

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
	public HeaterManager manager; 
	
}
