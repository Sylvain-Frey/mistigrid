package fr.tpt.s3.microSmartGridSimulation.layout.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

import fr.tpt.s3.microSmartGridSimulation.layout.Layout;
import fr.tpt.s3.microSmartGridSimulation.layout.OpeningLayout;
import fr.tpt.s3.microSmartGridSimulation.temperature.Opening;

@Component(name="WallLayout",immediate=true)
@Provides(specifications={OpeningLayout.class,Layout.class})
public class WallLayoutImpl implements OpeningLayout {

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
	public void open() {
		opening.open();		
	}

	@Override
	public void close() {
		opening.close();
	}

	@Override
	public boolean isOpen() {
		return opening.isOpen();
	}

	@Override
	public boolean isClosed() {
		return opening.isClosed();
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
	
	@Requires(id="opening")
	public Opening opening;

}
