/*******************************************************************************
 * Copyright (c) 2013 EDF. This software was developed with the 
 * collaboration of Télécom ParisTech (Dragutin Brezak, Sylvain Frey).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Dragutin Brezak, Sylvain Frey - initial API and implementation
 ******************************************************************************/
package fr.sylfrey.misTiGriD.arduino.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

import fr.sylfrey.misTiGriD.arduino.ArduinoThermicObject;
import fr.sylfrey.misTiGriD.arduino.serialConnection.ArduinoSerialWriterPort0;
import fr.sylfrey.misTiGriD.arduino.serialConnection.ArduinoSerialWriterPort1;
import fr.sylfrey.misTiGriD.arduino.serialConnection.ArduinoSerialWriterPort2;
import fr.sylfrey.misTiGriD.environment.Time;
import fr.sylfrey.misTiGriD.environment.Updatable;
import fr.sylfrey.misTiGriD.temperature.ThermicObject;

/**
 * This class implements atmosphere. Atmosphere temperature is updated
 * from Arduino Thermistor sensor that is placed outside the house model (in  the atmosphere).
 * @author dragutin
 */
@Component(name="ArduinoAtmosphere",immediate=true)
@Provides(specifications={Updatable.class, ArduinoThermicObject.class, ThermicObject.class})
public class ArduinoAtmosphere implements Updatable, ArduinoThermicObject {

	
	@Override
	public String getName() {
		return name;
	}
	
	
	/**
	 * The real atmosphere temperature from Arduino Thermistor sensor.
	 * This method is called by ArduinoSerialReader in order the refresh temperature's value.
	 * @param real environment temperature value
	 */
	@Override
	public void setCurrentTemperature(float temperature) {
		this.temperature = temperature;
	}


	/**
	 * Returns real environment temperature value (sensor reading).
	 * @return real environment temperature value
	 */
	@Override
	public float getCurrentTemperature() {
		return temperature;
	}
	
	@Override
	public int getPeriod() {
		return period;
	}
	
	
	/**
	 * Starts a new thread that asks for environment temperature update from Arduino.
	 * Parameter "getCurrentTempCode" is predefined in metadata.xml file.
	 */
	@Override
	public void update() {
		 if(arduinoBoardCode==0){
			 (new Thread(new ArduinoSerialWriterPort0(getCurrentTempCode))).start();
    	 }
    	 else if(arduinoBoardCode==1){
    		 (new Thread(new ArduinoSerialWriterPort1(getCurrentTempCode))).start();
    	 }
    	 else if(arduinoBoardCode==2){
    		 (new Thread(new ArduinoSerialWriterPort2(getCurrentTempCode))).start();
    	 }		
	}
	
		
	@Requires
	public Time time;

	@Property(name="instance.name",mandatory=true)
	public String name;
		
	@Property(mandatory=true)
	public int period;
	
	@Property(mandatory=true)
	public float temperature;		
	
	@Property(mandatory=true)
	public int getCurrentTempCode;

	@Property(mandatory=true)
	public int arduinoBoardCode;
	
}
