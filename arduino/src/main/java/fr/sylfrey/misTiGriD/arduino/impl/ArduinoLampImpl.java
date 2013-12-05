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

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;

import fr.sylfrey.misTiGriD.arduino.ArduinoLamp;
import fr.sylfrey.misTiGriD.arduino.ArduinoThermicObject;
import fr.sylfrey.misTiGriD.arduino.serialConnection.ArduinoSerialWriterPort0;
import fr.sylfrey.misTiGriD.arduino.serialConnection.ArduinoSerialWriterPort1;
import fr.sylfrey.misTiGriD.arduino.serialConnection.ArduinoSerialWriterPort2;
import fr.sylfrey.misTiGriD.electricalGrid.Aggregator;
import fr.sylfrey.misTiGriD.electricalGrid.BlackOut;
import fr.sylfrey.misTiGriD.electricalGrid.Lamp;
import fr.sylfrey.misTiGriD.electricalGrid.Prosumer;

/**
 * This class implements lamp component. Lamps are specified in metadata.xml file.
 * @author dragutin
 *
 */
@Component(name="ArduinoLamp",immediate=true)
@Provides(specifications={ArduinoLamp.class, Lamp.class, Prosumer.class})
public class ArduinoLampImpl implements ArduinoLamp {

	
	@Override
	public void setProsumedPower(float power) {
		_setProsumedPower(power);
	}

	/**
	 * @return prosumedPower
	 */
	@Override
	public float getProsumedPower() {
		_getProsumedPower();
		return prosumedPower;
	}


	@Override
	public void blackout() {
		System.out.println("# " + name + " : blackout!");
		prosumedPower = 0;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public float getEmissionPower() {		
		return -getProsumedPower();
	}
	

	@Override
	public void setEmissionPower(float power) {
		float effectivePower = 0;
		effectivePower = Math.max(0,power);
		effectivePower = Math.min(maxEmissionPower,power);
		setProsumedPower(-effectivePower);
	}

	@Override
	public float getMaxEmissionPower() {
		return maxEmissionPower;
	}
	
	
	protected void _setProsumedPower(float power) {
		prosumedPower = power;
		if (aggregator == null) { return; }
		try {
			aggregator.updateProsumption(this, power);
		} catch (BlackOut b) {
			blackout();
		}
	}
	
	/**
	 * Creates new ArduinoSerialWriter Thread and requests to know lamp power consumption from
	 * Arduino board. Parameters "getLampPowerCode" and "arduinoBoardCode" is defined in "metadata.xml" file
	 * and for every lamp it has different value.
	 */
	private void _getProsumedPower() {		
		if(arduinoBoardCode==0){
			 (new Thread(new ArduinoSerialWriterPort0(getLampPowerCode))).start();
	 	}
	 	else if(arduinoBoardCode==1){
	 		(new Thread(new ArduinoSerialWriterPort1(getLampPowerCode))).start();
	 	}
	 	else if(arduinoBoardCode==2){
	 		(new Thread(new ArduinoSerialWriterPort2(getLampPowerCode))).start();
	 	}
	}
	
	@Bind(id="aggregator")
	public void connectTo(Aggregator aggregator) {
		aggregator.connect(this);
		this.aggregator = aggregator;
		_setProsumedPower(prosumedPower);
//		if(this.getName().equals("lamp_kitchen")){
//			_setProsumedPower(arduinoKitchenLampPower);
//		}else if(this.getName().equals("lamp_wc")){
//			_setProsumedPower(arduinoWCLampPower);
//		}	
//		_setProsumedPower(arduinoKitchenLampPower);
	}

	@Unbind(id="aggregator")
	public void disconnectFrom(Aggregator aggregator) {
		if (aggregator!=this.aggregator) {
			System.err.println("# warning : bad aggregator match in " + name);
		}
		setProsumedPower(0);
		aggregator.disconnect(this);
		this.aggregator = null;
	}

	
	/**
	 * This method is used only if lamp is supposed to be turned on from GUI.
	 * Of so, this method is called. For now, only kitchen lamp has this option.
	 * Parameters "turnLampOn" and "arduinoBoardCode" are defined in "metadata.xml"
	 * file.
	 */
	@Override
	public void turnOn() {
		if(arduinoBoardCode==0){
			 (new Thread(new ArduinoSerialWriterPort0(turnLampOn))).start();
	 	}
	 	else if(arduinoBoardCode==1){
	 		(new Thread(new ArduinoSerialWriterPort1(turnLampOn))).start();
	 	}
	 	else if(arduinoBoardCode==2){
	 		(new Thread(new ArduinoSerialWriterPort2(turnLampOn))).start();
	 	}

	}

	/**
	 * This method is used only if lamp is supposed to be turned off from GUI.
	 * Of so, this method is called. For now, only kitchen lamp has this option.
	 * Parameters "turnLampOff" and "arduinoBoardCode" are defined in "metadata.xml"
	 * file.
	 */
	@Override
	public void turnOff() {
		if(arduinoBoardCode==0){
			 (new Thread(new ArduinoSerialWriterPort0(turnLampOff))).start();
	 	}
	 	else if(arduinoBoardCode==1){
	 		(new Thread(new ArduinoSerialWriterPort1(turnLampOff))).start();
	 	}
	 	else if(arduinoBoardCode==2){
	 		(new Thread(new ArduinoSerialWriterPort2(turnLampOff))).start();
	 	}

	}

	

	@Property(name="instance.name")
	public String name;

	@Property
	public float prosumedPower;

	@Property
	public float maxEmissionPower;	
	
	@Requires(id="aggregator")
	public Aggregator aggregator;	
	
	@Requires(id="room")
	public ArduinoThermicObject room;
	
	@Property(mandatory=true)
	public int getLampPowerCode;
	
	@Property
	public int turnLampOn;
	
	@Property
	public int turnLampOff;
	
	@Property(mandatory=true)
	public int arduinoBoardCode;

}
