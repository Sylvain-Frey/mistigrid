package fr.tpt.s3.microSmartGridSimulation.arduino.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;

import fr.tpt.s3.microSmartGridSimulation.arduino.ArduinoThermicObject;
import fr.tpt.s3.microSmartGridSimulation.arduino.serialConnection.ArduinoSerialWriterPort0;
import fr.tpt.s3.microSmartGridSimulation.arduino.serialConnection.ArduinoSerialWriterPort1;
import fr.tpt.s3.microSmartGridSimulation.arduino.serialConnection.ArduinoSerialWriterPort2;
import fr.tpt.s3.microSmartGridSimulation.environment.Updatable;
import fr.tpt.s3.microSmartGridSimulation.temperature.ThermicObject;

/**
 * This class implements room components. Rooms are specified in metadata.xml file.
 * @author dragutin
 * 
 */
@Component(name="ArduinoRoom",immediate=true)
@Provides(specifications={Updatable.class, ArduinoThermicObject.class, ThermicObject.class})
public class ArduinoRoom implements Updatable, ArduinoThermicObject {

	

	@Override
	public void setCurrentTemperature(float temperature) {
		this.temperature=temperature;
	}


	/**
	 * Updates the temperature value (sensor reading).
	 * @return real room temperature
	 */
	@Override
	public float getCurrentTemperature() {
		return temperature;
	}
	
	@Override
	public String getName() {
		return name;
	}
		
	/**
	 * Starts a new thread that asks temperature update from Arduino. Parameters "arduinoBoardCode"
	 * and "getCurrentRoomTempCode" for each room are defined in "metadata.xml" file.
	 */
	@Override
	public void update() {
		if(arduinoBoardCode==0){
			 (new Thread(new ArduinoSerialWriterPort0(getCurrentRoomTempCode))).start();
 	 	}
 	 	else if(arduinoBoardCode==1){
 	 		(new Thread(new ArduinoSerialWriterPort1(getCurrentRoomTempCode))).start();
 	 	}
 	 	else if(arduinoBoardCode==2){
 	 		(new Thread(new ArduinoSerialWriterPort2(getCurrentRoomTempCode))).start();
 	 	}
 	 	else if(arduinoBoardCode==3){
 	 		//DO NOTHING, USED FOR LIVING ROOM whose temperature is updated
 	 		//by taking mean value between two thermometers that are place inside
 	 		//living room
 	 	}
	}
		
	@Property(name="instance.name",mandatory=true)
	public String name;
	
	@Property(mandatory=true)
	public float temperature;
	
	@Property(mandatory=true)
	public int period;

	@Override
	public int getPeriod() {
		return period;
	}
	
	@Property(mandatory=true)
	public int getCurrentRoomTempCode;
	
	@Property(mandatory=true)
	public int arduinoBoardCode;;

	
}