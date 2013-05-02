package fr.sylfrey.misTiGriD.layout.impl;

import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

import fr.sylfrey.misTiGriD.electricalGrid.Storage;
import fr.sylfrey.misTiGriD.layout.Layout;
import fr.sylfrey.misTiGriD.layout.StorageLayout;

@org.apache.felix.ipojo.annotations.Component(name="StorageLayout")
@Provides(specifications={StorageLayout.class, Layout.class})
public class StorageLayoutImpl implements StorageLayout {
	
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
	
	@Requires(id="storage")
	public Storage storage; 

	@Override 
	public String getName() {
		return storage.getName();
	}

	@Override 
	public void setProsumedPower(float power) {
		storage.setProsumedPower(power);
	}

	@Override 
	public float getProsumedPower() {
		return storage.getProsumedPower();
	}

	@Override 
	public State getState() {
		return storage.getState();
	}

	@Override 
	public void blackout() {
		storage.blackout();
	}

	@Override 
	public void setState(State state) {
		storage.setState(state);
	}

	@Override 
	public float getLoad() {
		return storage.getLoad();
	}

	@Override 
	public float getLoadCapacity() {
		return storage.getLoadCapacity();
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
	
}
