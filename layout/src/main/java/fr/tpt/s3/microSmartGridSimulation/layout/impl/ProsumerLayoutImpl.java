package fr.tpt.s3.microSmartGridSimulation.layout.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

import fr.tpt.s3.microSmartGridSimulation.electricalGrid.Prosumer;
import fr.tpt.s3.microSmartGridSimulation.layout.Layout;
import fr.tpt.s3.microSmartGridSimulation.layout.ProsumerLayout;

@Component(name="ProsumerLayout",immediate=true)
@Provides(specifications={ProsumerLayout.class,Layout.class})
public class ProsumerLayoutImpl implements ProsumerLayout {

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
	public float getProsumedPower() {
		return prosumer.getProsumedPower();
	}

	@Override
	public void blackout() {
		prosumer.blackout();
	}

	@Override
	public String getName() {
		return prosumer.getName();
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

	@Requires(id="prosumer")
	public Prosumer prosumer;

}
