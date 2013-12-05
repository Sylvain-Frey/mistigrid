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

import fr.sylfrey.misTiGriD.arduino.Fan;
import fr.sylfrey.misTiGriD.electricalGrid.Aggregator;
import fr.sylfrey.misTiGriD.electricalGrid.BlackOut;
import fr.sylfrey.misTiGriD.electricalGrid.Prosumer;
import fr.sylfrey.misTiGriD.temperature.ThermicObject;

/**
 * This class imlements Fan component but it is not being used. Modifications are necessary
 * in order to use this component (therefore this code won't be documented).
 * @author dragutin
 *
 */
@Component(name="ArduinoFan",immediate=true)
@Provides(specifications={Fan.class,Prosumer.class})
public class ArduinoFan implements Fan {


	@Override
	public void setProsumedPower(float power) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getProsumedPower() {
		// TODO Auto-generated method stub
		return 0;
	};

	
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
	
		
//	private void _getProsumedPower() {		
//		
//		if(arduinoBoardCode==0){
//			 (new Thread(new ArduinoSerialWriterPort0(getFanPowerCode))).start();
//   	 	}
//   	 	else if(arduinoBoardCode==1){
//   	 		(new Thread(new ArduinoSerialWriterPort1(getFanPowerCode))).start();
//   	 	}
//   	 	else if(arduinoBoardCode==2){
//   	 		(new Thread(new ArduinoSerialWriterPort2(getFanPowerCode))).start();
//   	 	}
//	}
	
	@Bind(id="aggregator")
	public void connectTo(Aggregator aggregator) {
		aggregator.connect(this);
		this.aggregator = aggregator;
		_setProsumedPower(prosumedPower);
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
	
	@Property(name="instance.name")
	public String name;

	@Property
	public float prosumedPower;

	@Property
	public float maxEmissionPower;	
	
	@Requires(id="aggregator")
	public Aggregator aggregator;	
	
	@Requires(id="room")
	public ThermicObject room;
	
	@Property(mandatory=true)
	public int getFanPowerCode;

	@Property(mandatory=true)
	public int arduinoBoardCode;

}
