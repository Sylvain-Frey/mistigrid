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


import java.util.List;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;

import fr.sylfrey.misTiGriD.appliances.Heater;
import fr.sylfrey.misTiGriD.arduino.ArduinoHeater;
import fr.sylfrey.misTiGriD.arduino.ArduinoThermicObject;
import fr.sylfrey.misTiGriD.arduino.serialConnection.ArduinoSerialWriterPort0;
import fr.sylfrey.misTiGriD.arduino.serialConnection.ArduinoSerialWriterPort1;
import fr.sylfrey.misTiGriD.arduino.serialConnection.ArduinoSerialWriterPort2;
import fr.sylfrey.misTiGriD.electricalGrid.Aggregator;
import fr.sylfrey.misTiGriD.electricalGrid.BlackOut;
import fr.sylfrey.misTiGriD.electricalGrid.Prosumer;

/**
 * This class implements heater component. Heaters are specified in metadata.xml file.
 * @author dragutin
 *
 */
@Component(name="ArduinoHeater",immediate=true)
@Provides(specifications={ArduinoHeater.class,Prosumer.class, Heater.class})
public class ArduinoHeaterImpl implements ArduinoHeater {

	
	/**
	 * Static method that returns new power for heater in the kitchen. This method is called
	 * by ArduinoSerialWriter component and this new power is sent to Arduino board.
	 * @return new power for kitchen heater
	 */
	public static float getpPowerKitchen() {
		return pPowerKitchen;
	}

	/**
	 * Static method that returns new power for heater in the bathroom. This method is called
	 * by ArduinoSerialWriter component and this new power is sent to Arduino board.
	 * @return new power for bathroom heater
	 */
	public static float getpPowerBathroom() {
		return pPowerBathroom;
	}

	/**
	 * Static method that returns new power for heater in the bedroom1. This method is called
	 * by ArduinoSerialWriter component and this new power is sent to Arduino board.
	 * @return new power for bedroom1 heater
	 */
	public static float getpPowerBedroom1() {
		return pPowerBedroom1;
	}

	/**
	 * Static method that returns new power for heater in the bedroom2. This method is called
	 * by ArduinoSerialWriter component and this new power is sent to Arduino board.
	 * @return new power for bedroom2 heater
	 */
	public static float getpPowerBedroom2() {
		return pPowerBedroom2;
	}

	/**
	 * Static method that returns new power for heater in the bedroom3. This method is called
	 * by ArduinoSerialWriter component and this new power is sent to Arduino board.
	 * @return new power for bedroom3 heater
	 */
	public static float getpPowerBedroom3() {
		return pPowerBedroom3;
	}


	/**
	 * Static method that returns new power for heater in the living room (heater 1). This method is called
	 * by ArduinoSerialWriter component and this new power is sent to Arduino board.
	 * @return new power for living room (heater 1)
	 */
	public static float getpPowerLivingRoom1() {
		return pPowerLivingRoom1;
	}

	/**
	 * Static method that returns new power for heater in the living room (heater 2). This method is called
	 * by ArduinoSerialWriter component and this new power is sent to Arduino board.
	 * @return new power for living room (heater 2)
	 */
	public static float getpPowerLivingRoom2() {
		return pPowerLivingRoom2;
	}

	/**
	 * Static method that returns new power for heater in the entrance. This method is called
	 * by ArduinoSerialWriter component and this new power is sent to Arduino board.
	 * @return new power for entrance heater
	 */
	public static float getpPowerEntrance() {
		return pPowerEntrance;
	}

	
//	/**
//	 * Not used with heater component.
//	 */
//	@Override
//	public void setCurrentTemperature(float temperature) {
//		// TODO Auto-generated method stub
//	}
	
	@Override
	public float getEmissionPower() {
		return -getProsumedPower();
	}

	@Override
	public float getMaxEmissionPower() {
		return maxEmissionPower;
	}

	/**
	 * This method is called by heater manager or any other component that wants to set
	 * new heater emission power. 
	 */
	@Override
	public void setEmissionPower(float power) {
		float effectivePower = 0;
		effectivePower = Math.max(0,power);
		effectivePower = Math.min(maxEmissionPower,power);		
		
		if(this.getName().equals("heater_bedroom1")){
			pPowerBedroom1=effectivePower;
		}
		else if(this.getName().equals("heater_bedroom2")){
			pPowerBedroom2=effectivePower;
		}
		else if(this.getName().equals("heater_bedroom3")){
			pPowerBedroom3=effectivePower;
		}
		else if(this.getName().equals("heater_bathroom")){
			pPowerBathroom=effectivePower;
		}
		else if(this.getName().equals("heater_livingRoom1")){
			pPowerLivingRoom1=effectivePower;
		}
		else if(this.getName().equals("heater_livingRoom2")){
			pPowerLivingRoom2=effectivePower;
		}
		else if(this.getName().equals("heater_entrance")){
			pPowerEntrance=effectivePower;
		}
		else if(this.getName().equals("heater_kitchen")){
			pPowerKitchen=effectivePower;
		}
		
		setProsumedPower(-effectivePower);
	}	
	
	@Override
	public float getCurrentTemperature() {
		return room.getCurrentTemperature();
	}
	
	
	@Override
	public String getName() {
		return name;
	}	

	@Override
	public float getProsumedPower() {
		return prosumedPower;
	}
	
	@Override
	public void blackout() {
		System.out.println("# " + name + " : blackout!");
		prosumedPower = 0;
	}

	@Override
	public void setProsumedPower(float power) {
		try {
			_setProsumedPower(power);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	

	@Bind(id="aggregator")
	public void connectTo(Aggregator aggregator){
		aggregator.connect(this);
		this.aggregator = aggregator;
		try {
			_setProsumedPower(prosumedPower);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	 * Added method updateHeating(float newPower) which updates real house heater's power.
	 * @param power
	 */
	protected void _setProsumedPower(float power){
		try {
			updateHeating(power);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		prosumedPower=power;
		if (aggregator == null) { return; }
		try {
			aggregator.updateProsumption(this, power);
		} catch (BlackOut b) {
			blackout();
		}
	}
	
	
	/**
	 * Creates new ArduinoSerialWriter Thread and requests heater power update on
	 * Arduino board. Parameter "setHeatingPowerCode" and "arduinoBoardCode" is defined in "metadata.xml" file
	 * and for every heater it has different value.
     */
	public void updateHeating(float newPower) throws Exception{

		if(arduinoBoardCode==0){
			 (new Thread(new ArduinoSerialWriterPort0(setHeatingPowerCode))).start();
  	 	}
  	 	else if(arduinoBoardCode==1){
  	 		(new Thread(new ArduinoSerialWriterPort1(setHeatingPowerCode))).start();
  	 	}
  	 	else if(arduinoBoardCode==2){
  	 		(new Thread(new ArduinoSerialWriterPort2(setHeatingPowerCode))).start();
  	 	}
	}
	
	
	@Property(mandatory=true)
	public int setHeatingPowerCode;
	
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
	public int arduinoBoardCode;;
	
	private static float pPowerBathroom=0;
	private static float pPowerBedroom1=0;
	private static float pPowerBedroom2=0;
	private static float pPowerBedroom3=0;
	private static float pPowerLivingRoom1=0;
	private static float pPowerLivingRoom2=0;
	private static float pPowerEntrance=0;
	private static float pPowerKitchen=0;
	
	@Override
	public float getHeatConductance() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<fr.sylfrey.misTiGriD.temperature.ThermicObject> getNeighbours() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
