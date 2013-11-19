/*******************************************************************************
 * Copyright (c) 2013 Sylvain Frey.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Dragutin Brezak - initial API and implementation
 *     Sylvain Frey - initial API and implementation
 ******************************************************************************/
package fr.sylfrey.misTiGriD.arduino.impl;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;

import fr.sylfrey.misTiGriD.arduino.serialConnection.ArduinoSerialWriterPort2;
import fr.sylfrey.misTiGriD.electricalGrid.Aggregator;
import fr.sylfrey.misTiGriD.electricalGrid.BlackOut;
import fr.sylfrey.misTiGriD.electricalGrid.Prosumer;
import fr.sylfrey.misTiGriD.electricalGrid.TunableProsumer;
import fr.sylfrey.misTiGriD.environment.Updatable;


/**
 * This class implements solar panel component. Rooms are specified in "metadata.xml" file.
 * @author dragutin
 *
 */
@Component(name="ArduinoSolarPanel")
@Provides(specifications={TunableProsumer.class,Updatable.class, Prosumer.class})
public class SolarPanel implements TunableProsumer, Updatable{

	
	@Override
	public String getName() {
		return name;		
	}
	
	
	@Override
	public void setProsumedPower(float power) {
		prosumedPower=power;
	}

	
	@Override
	public float getProsumedPower() {
		return prosumedPower;
	}
	

	@Override
	public void blackout() {
		System.out.println("# solar panel " + name + " : blackout!");
	}
	
	
	/**
	 * Starts a new thread that asks temperature update from Arduino.
	 * Parameter "getCurrentTempCode" is specified in "metadata.xml" file.
	 */
	@Override
	public void update() {
		if(running){
			(new Thread(new ArduinoSerialWriterPort2(getCurrentBrightnessCode))).start();
	   	 	_updateProsumption();
		}
		
	}
	
	
	
	public void _updateProsumption() {
		/* TODO max prosumed power can be 1000 and max arduinoSensorBrightness can be 1023
		 *  There may be need to change this little bit
		 */
		try {
			aggregator.updateProsumption(this, prosumedPower);
		} catch (BlackOut b) {
			b.printStackTrace();
		}		
	}
	
	
	
	@Bind(id="aggregator")
	public void connectTo(Aggregator aggregator) {
		aggregator.connect(this);
		try {
			aggregator.updateProsumption(this, prosumedPower);
			this.aggregator = aggregator;
		} catch (BlackOut b) {
			b.printStackTrace();
		}
	}
	
	

	@Unbind(id="aggregator")
	public void disconnectFrom(Aggregator aggregator) {
		if (aggregator!=this.aggregator) {
			System.err.println("# warning : bad aggregator match in " + name);
		}
		try {
			aggregator.updateProsumption(this, 0);
			aggregator.disconnect(this);
			this.aggregator = null;
		} catch (BlackOut b) {
			b.printStackTrace();
		}
	}
	

	@Validate
	public void start() {		
		running = true;
		prosumedPower=arduinoSensorBrightness;
	}
	

	@Invalidate
	public void stop(){
		running = false;
	}
	
	
	
	@Property(mandatory=true)
	public int period;

	@Override
	public int getPeriod() {
		return period;
	}
	
	@Requires
	private Aggregator aggregator;
			
	@Property(name="instance.name")
	protected String name;
	
	@Property(mandatory=true)
	private int arduinoSensorBrightness;
		

	@Property(mandatory=true)
	public int getCurrentBrightnessCode;
	
	private float prosumedPower;
	private boolean running;

}
