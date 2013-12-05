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

import fr.sylfrey.misTiGriD.arduino.ArduinoLamp;
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
@Component(name="ArduinoSerialReaderPort1",immediate=true)
public class ArduinoSerialReaderPort1 implements SerialPortEventListener {

	
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
 				
 				
 				if(spl1.equals("1")){
					if(firstLivingRoom1){
						float newTemp=Float.valueOf(spl2.trim()).floatValue();
						livingRoom1.setCurrentTemperature(newTemp);
						rightLivingRoom1Temp=newTemp; // FOR MEAN VALUE
						oldLivingRoom1Temp=newTemp;
						firstLivingRoom1=false;
					}else{
						float newLivingRoomTemp=Float.valueOf(spl2.trim()).floatValue();
						if((oldLivingRoom1Temp-newLivingRoomTemp<-0.25) || (oldLivingRoom1Temp-newLivingRoomTemp>0.25)){
							counterLivingRoom1++;
							livingRoom1.setCurrentTemperature(oldLivingRoom1Temp);
							rightLivingRoom1Temp=oldLivingRoom1Temp; // FOR MEAN VALUE
							if(counterLivingRoom1>=20){
								firstLivingRoom1=true;
								counterLivingRoom1=0;
							}
						}else{
							livingRoom1.setCurrentTemperature(newLivingRoomTemp);
							rightLivingRoom1Temp=newLivingRoomTemp; // FOR MEAN VALUE
							oldLivingRoom1Temp=newLivingRoomTemp;
							counterLivingRoom1=0;
						} 						
					}
					
					if(rightLivingRoom1Temp !=0 && rightLivingRoom2Temp != 0){
						livingRoom.setCurrentTemperature((float)(rightLivingRoom1Temp+rightLivingRoom2Temp)/2);
					}
				}
 				if(spl1.equals("2")){
					if(firstLivingRoom2){
						float newTemp=Float.valueOf(spl2.trim()).floatValue();
						livingRoom2.setCurrentTemperature(newTemp);
						rightLivingRoom2Temp=newTemp; // FOR MEAN VALUE
						oldLivingRoom2Temp=newTemp;
						firstLivingRoom2=false;
					}else{
						float newLivingRoomTemp=Float.valueOf(spl2.trim()).floatValue();
						if((oldLivingRoom2Temp-newLivingRoomTemp<-0.25) || (oldLivingRoom2Temp-newLivingRoomTemp>0.25)){
							counterLivingRoom2++;
							livingRoom2.setCurrentTemperature(oldLivingRoom2Temp);
							rightLivingRoom2Temp=oldLivingRoom2Temp; // FOR MEAN VALUE
							if(counterLivingRoom2>=20){
								firstLivingRoom2=true;
								counterLivingRoom2=0;
							}
						}else{
							livingRoom2.setCurrentTemperature(newLivingRoomTemp);
							rightLivingRoom2Temp=newLivingRoomTemp; // FOR MEAN VALUE
							oldLivingRoom2Temp=newLivingRoomTemp;
							counterLivingRoom2=0;
						} 						
					} 					
				} 				
 				else if(spl1.equals("3")){
 					if(firstTimeKitchen){
						float newTemp=Float.valueOf(spl2.trim()).floatValue();
						kitchen.setCurrentTemperature(newTemp);
						oldKitchenTemp=newTemp;
						firstTimeKitchen=false;
					}else{
						float newKitchenTemp=Float.valueOf(spl2.trim()).floatValue();
						if((oldKitchenTemp-newKitchenTemp<-0.25) || (oldKitchenTemp-newKitchenTemp>0.25)){
							counterKitchen++;
							kitchen.setCurrentTemperature(oldKitchenTemp);
							if(counterKitchen>=20){
								firstTimeKitchen=true;
								counterKitchen=0;
							}
						}else{
							kitchen.setCurrentTemperature(newKitchenTemp);
							oldKitchenTemp=newKitchenTemp;
							counterKitchen=0;
						} 						
					} 					
				} 				
				else if(spl1.equals("4")){
					if(firstTimeWC){
						float newTemp=Float.valueOf(spl2.trim()).floatValue();
						wc.setCurrentTemperature(newTemp);
						oldWCTemp=newTemp;
						firstTimeWC=false;
					}else{
						float newWCTemp=Float.valueOf(spl2.trim()).floatValue();
						if((oldWCTemp-newWCTemp<-0.25) || (oldWCTemp-newWCTemp>0.25)){
							counterWC++;
							wc.setCurrentTemperature(oldWCTemp);
							if(counterWC>=20){
								firstTimeWC=true;
								counterWC=0;
							}
						}else{
							wc.setCurrentTemperature(newWCTemp);
							oldWCTemp=newWCTemp;
							counterWC=0;
						} 						
					} 					
				} 
				else if(spl1.equals("11")){
//					System.out.println("!!!!!!!!!!!!!!!" + spl2);
 					float power=Float.valueOf(spl2.trim()).floatValue();
 					float realPower=(float) (power/17.05);
 					lampBathroom.setEmissionPower(realPower);
 				}
				else if(spl1.equals("12")){
					
 					float power=Float.valueOf(spl2.trim()).floatValue();
 					float realPower=(float) (power/17.05);
 					lampWC.setEmissionPower(realPower);
 				}
 				
// 				if(spl1.equals("1")){
// 					float power=Float.valueOf(spl2.trim()).floatValue();
// 					float realPower=(float) (power/17.05);
// 					ArduinoLamp.setUpdatedKitchenLampPower(realPower);
// 				}
// 				else if(spl1.equals("2")){
// 					float power=Float.valueOf(spl2.trim()).floatValue();
// 					float realPower=(float) (power/17.05);
// 					ArduinoLamp.setUpdatedWCLampPower(realPower);
// 				}
// 				else if(spl1.equals("3")){
// 					System.out.println("The door opened!");
// 					
// 				}else if(spl1.equals("4")){
// 					System.out.println("The door closed!");
// 				 					
// 				}
// 				else if(spl1.equals("6")){
// 					SolarPanel.setUpdatedsensorBrightness(Integer.valueOf(spl2.trim()).intValue()); 
// 				}
 			
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
	
	private float oldLivingRoom1Temp;
	private boolean firstLivingRoom1=true;
	
	private float oldLivingRoom2Temp;
	private boolean firstLivingRoom2=true;
	
	private float oldKitchenTemp;
	private boolean firstTimeKitchen=true;
	
	private float oldWCTemp;
	private boolean firstTimeWC=true;	
	
	private int counterLivingRoom1=0;
	private int counterLivingRoom2=0;
	private int counterKitchen=0;
	private int counterWC=0;

	private float rightLivingRoom1Temp=0;
	private float rightLivingRoom2Temp=0;	
	
	
	@Property(mandatory=true)
	public String portName;
	

	@Requires(id="room1")
	public ArduinoThermicObject livingRoom1;
	
	@Requires(id="room2")
	public ArduinoThermicObject livingRoom2;
	
	@Requires(id="room3")
	public ArduinoThermicObject kitchen;
	
	@Requires(id="room4")
	public ArduinoThermicObject wc;
	
	@Requires(id="room5")
	public ArduinoThermicObject livingRoom;

	
	@Requires(id="lamp1")
	public ArduinoLamp lampBathroom;
	
	@Requires(id="lamp2")
	public ArduinoLamp lampWC;

	
}
