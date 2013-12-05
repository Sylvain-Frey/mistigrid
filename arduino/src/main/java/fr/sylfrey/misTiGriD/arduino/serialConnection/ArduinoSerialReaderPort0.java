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
package fr.sylfrey.misTiGriD.arduino.serialConnection;

import fr.sylfrey.misTiGriD.arduino.ArduinoThermicObject;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

/**
 * Component responsible for receiving data from Arduino.
 * @author dragutin
 *
 */
@Component(name="ArduinoSerialReaderPort0",immediate=true)
public class ArduinoSerialReaderPort0 implements SerialPortEventListener {

	
	/**
	 * Opens port in order to establish serial connection with Arduino
	 * Creates EventListener
	 * 
	 * @throws NoSuchPortException
	 * @throws UnsupportedCommOperationException
	 * @throws PortInUseException
	 * @throws IOException
	 * @throws TooManyListenersException
	 * @throws InterruptedException
	 */
	@Validate
	public void start() throws NoSuchPortException, UnsupportedCommOperationException, PortInUseException, IOException, TooManyListenersException, InterruptedException{		
		CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portName);	
		if (portId.isCurrentlyOwned() )
	       {
	           System.out.println("Error: Port is currently in use");
	       }	 
		 	     
		serialPort = (SerialPort)portId.open(this.getClass().getName(), 2000);
        serialPort.setSerialPortParams(9600,
        		SerialPort.DATABITS_8,
		   		SerialPort.STOPBITS_1,
		   		SerialPort.PARITY_NONE);
		       
        in=serialPort.getInputStream();
        out=serialPort.getOutputStream();
        
        serialPort.addEventListener(this);
		serialPort.notifyOnDataAvailable(true);
		
//		Thread.sleep(2000);
	}
	
	/**
	 * Closes serial port
	 */
	@Invalidate
	public void stop(){
		try {
			in.close();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		serialPort.close();
	}
	
	
	
	/**
	 * EventListener - listens serialPort and whenever arduino sends data this method is invoked
	 */
	@Override
	public void serialEvent(SerialPortEvent oEvent) {
		
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
 			try {
 			
 				Thread.sleep(20); 				
 				int available = in.available();
 				byte chunk[] = new byte[available];
 				in.read(chunk, 0, available); 				
 				String received=new String(chunk);
 				String[] split=received.split(":");
 				String spl1=split[0];
 				String spl2=split[1];
// 				System.out.println(spl1 + ": " + spl2); 
 				
 				if(spl1.equals("1")){
 					if(firstTimeEnvironment){
// 						float newTemp=Float.valueOf(spl2.trim()).floatValue();
 						float newTemp=Float.parseFloat(spl2.trim());
// 						ArduinoAtmosphere.setCurrentTemperature(newTemp);
 						atmosphere.setCurrentTemperature(newTemp);
 						oldEnvironmentTemp=newTemp;
 						firstTimeEnvironment=false;
// 						System.out.println(newTemp); 						
 					}else{
// 						float newEnvironmentTemp=Float.valueOf(spl2.trim()).floatValue();
 						float newEnvironmentTemp=Float.parseFloat(spl2.trim());;
 						if((oldEnvironmentTemp-newEnvironmentTemp<-0.25) || (oldEnvironmentTemp-newEnvironmentTemp>0.25)){
 							counterEnvironment++;
// 							ArduinoAtmosphere.setCurrentTemperature(oldEnvironmentTemp);
 							atmosphere.setCurrentTemperature(oldEnvironmentTemp);
// 							System.out.println("This one is old: " + oldEnvironmentTemp + "  " + counterEnvironment);
 							if(counterEnvironment>=20){
 								firstTimeEnvironment=true;
 								counterEnvironment=0;
 							}
// 							System.out.println(oldEnvironmentTemp);
 						}else{
// 							ArduinoAtmosphere.setCurrentTemperature(newEnvironmentTemp);
 							atmosphere.setCurrentTemperature(newEnvironmentTemp);
// 							System.out.println("This one is new: " + newEnvironmentTemp);
 							oldEnvironmentTemp=newEnvironmentTemp;
 							counterEnvironment=0;
// 							System.out.println(newEnvironmentTemp);
 						} 						
 					} 					
 				}
 				else if(spl1.equals("2")){
// 					System.out.println(spl2);
 					if(firstTimeBedroom1){
// 						float newTemp=Float.valueOf(spl2.trim()).floatValue();
 						float newTemp=Float.parseFloat(spl2.trim());
 						bedroom1.setCurrentTemperature(newTemp);
 						oldBedroom1Temp=newTemp;
 						firstTimeBedroom1=false;
 					}else{
// 						float newBedroomTemp=Float.valueOf(spl2.trim()).floatValue();
 						float newBedroomTemp=Float.parseFloat(spl2.trim());;
 						if((oldBedroom1Temp-newBedroomTemp<-0.25) || (oldBedroom1Temp-newBedroomTemp>0.25)){
 							counterBedroom1++;
 							bedroom1.setCurrentTemperature(oldBedroom1Temp);
 							if(counterBedroom1>=20){
 								firstTimeBedroom1=true;
 								counterBedroom1=0;
 							}
 						}else{
 							bedroom1.setCurrentTemperature(newBedroomTemp);
 							oldBedroom1Temp=newBedroomTemp;
 							counterBedroom1=0;
 						} 						
 					} 					
 				}
 				else if(spl1.equals("3")){
// 					System.out.println(spl2);
 					if(firstTimeBedroom2){
// 						float newTemp=Float.valueOf(spl2.trim()).floatValue();
 						float newTemp=Float.parseFloat(spl2.trim());
 						bedroom2.setCurrentTemperature(newTemp);
 						oldBedroom2Temp=newTemp;
 						firstTimeBedroom2=false;
 					}else{
// 						float newBedroomTemp=Float.valueOf(spl2.trim()).floatValue();
 						float newBedroomTemp=Float.parseFloat(spl2.trim());
 						if((oldBedroom2Temp-newBedroomTemp<-0.25) || (oldBedroom2Temp-newBedroomTemp>0.25)){
 							counterBedroom2++;
 							bedroom2.setCurrentTemperature(oldBedroom2Temp);
 							if(counterBedroom2>=20){
 								firstTimeBedroom2=true;
 								counterBedroom2=0;
 							}
 						}else{
 							bedroom2.setCurrentTemperature(newBedroomTemp);
 							oldBedroom2Temp=newBedroomTemp;
 							counterBedroom2=0;
 						} 						
 					} 					
 				}
 				else if(spl1.equals("4")){
// 					System.out.println(spl2);
 					if(firstTimeBedroom3){ 						
// 						float newTemp=Float.valueOf(spl2.trim()).floatValue();
 						float newTemp=Float.parseFloat(spl2.trim());
 						bedroom3.setCurrentTemperature(newTemp);
 						oldBedroom3Temp=newTemp;
 						firstTimeBedroom3=false;
 					}else{
// 						float newBedroomTemp=Float.valueOf(spl2.trim()).floatValue();
 						float newBedroomTemp=Float.parseFloat(spl2.trim());
 						if((oldBedroom3Temp-newBedroomTemp<-0.25) || (oldBedroom3Temp-newBedroomTemp>0.25)){
 							counterBedroom3++;
 							bedroom3.setCurrentTemperature(oldBedroom3Temp);
 							if(counterBedroom3>=20){
 								firstTimeBedroom3=true;
 								counterBedroom3=0;
 							}
 						}else{
 							bedroom3.setCurrentTemperature(newBedroomTemp);
 							oldBedroom3Temp=newBedroomTemp;
 							counterBedroom3=0;
 						} 						
 					} 					
 				}
 				else if(spl1.equals("5")){
// 					System.out.println(spl2);
 					if(firstTimeBathroom){
// 						float newTemp=Float.valueOf(spl2.trim()).floatValue();
 						float newTemp=Float.parseFloat(spl2.trim());
 						bathroom.setCurrentTemperature(newTemp);
 						oldBathroomTemp=newTemp;
 						firstTimeBathroom=false;
 					}else{
// 						float newBathroomTemp=Float.valueOf(spl2.trim()).floatValue();
 						float newBathroomTemp=Float.parseFloat(spl2.trim());
 						if((oldBathroomTemp-newBathroomTemp<-0.25) || (oldBathroomTemp-newBathroomTemp>0.25)){
 							counterBathroom++;
 							bathroom.setCurrentTemperature(oldBathroomTemp);
 							if(counterBathroom>=20){
 								firstTimeBathroom=true;
 								counterBathroom=0;
 							}
 						}else{
 							bathroom.setCurrentTemperature(newBathroomTemp);
 							oldBathroomTemp=newBathroomTemp;
 							counterBathroom=0;
 						} 						
 					} 					
 				}
 				else if(spl1.equals("6")){
// 					System.out.println(spl2);
 					if(firstTimeEntrance){
// 						float newTemp=Float.valueOf(spl2.trim()).floatValue();
 						float newTemp=Float.parseFloat(spl2.trim());
 						entrance.setCurrentTemperature(newTemp);
 						oldEntranceTemp=newTemp;
 						firstTimeEntrance=false;
 					}else{
// 						float newEntranceTemp=Float.valueOf(spl2.trim()).floatValue();
 						float newEntranceTemp=Float.parseFloat(spl2.trim());
 						if((oldEntranceTemp-newEntranceTemp<-0.25) || (oldEntranceTemp-newEntranceTemp>0.25)){
 							counterEntrance++;
 							entrance.setCurrentTemperature(oldEntranceTemp);
 							if(counterEntrance>=20){
 								firstTimeEntrance=true;
 								counterEntrance=0;
 							}
 						}else{
 							entrance.setCurrentTemperature(newEntranceTemp);
 							oldEntranceTemp=newEntranceTemp;
 							counterEntrance=0;
 						} 						
 					} 					
 				}
 							
// 				else if(spl1.equals("8")){
// 					float power=Float.valueOf(spl2.trim()).floatValue();
// 					float realPower=(float) (power/17.05);
// 					ArduinoFan.setUpdatedKitchenFanPower(realPower);
// 					System.out.println(realPower);
//				}
 				
 				else if(spl1.equals("7")){
// 					HeaterProba.setCurrentHeatingPower(Integer.valueOf(spl2.trim()).intValue());
// 					System.out.println(spl2); 
 				}
 				else if(spl1.equals("8")){
// 					HeaterProba.setCurrentHeatingPower(Integer.valueOf(spl2.trim()).intValue());
 				}
 				
 						 				 				 				
 			} catch (Exception e) {
 				System.err.println(e.toString());
 			}
 		}
	}
	
	
	public static OutputStream getOut() {
		return out;
	}
	
	private SerialPort serialPort;
	
	private InputStream in;
	
	private static OutputStream out;
	
	private float oldEnvironmentTemp;
	private boolean firstTimeEnvironment=true;	
//	private boolean secondTimeEnvironment=false;
	
	private float oldBedroom1Temp;
	private boolean firstTimeBedroom1=true;
	
	private float oldBedroom2Temp;
	private boolean firstTimeBedroom2=true;
	
	private float oldBedroom3Temp;
	private boolean firstTimeBedroom3=true;
	
	private float oldBathroomTemp;
	private boolean firstTimeBathroom=true;
	
	private float oldEntranceTemp;
	private boolean firstTimeEntrance=true;
	
	
	
//	private float oldKitchenTemp;
//	private boolean firstTimeKitchen=true;
//	
//	private float oldWCTemp;
//	private boolean firstTimeWC=true;	
//	
//	
//	private float oldLivingRoomTemp;
//	private boolean firstLivingRoom=true;

		
	
	private int counterEnvironment=0;
	private int counterBedroom1=0;
	private int counterBedroom2=0;
	private int counterBedroom3=0;	
	private int counterBathroom=0;
	private int counterEntrance=0;
	
//	private int counterKitchen=0;
//	private int counterWC=0;
//	private int counterLivingRoom=0;
	
	@Property(mandatory=true)
	public String portName;
	
	@Requires(id="room0")
	public ArduinoThermicObject atmosphere;
	
	@Requires(id="room1")
	public ArduinoThermicObject bedroom1;
	
	@Requires(id="room2")
	public ArduinoThermicObject bedroom2;
	
	@Requires(id="room3")
	public ArduinoThermicObject bedroom3;
	
	@Requires(id="room4")
	public ArduinoThermicObject bathroom;
	
	@Requires(id="room5")
	public ArduinoThermicObject entrance;
	
}
