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
import fr.sylfrey.misTiGriD.electricalGrid.TunableProsumer;
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
@Component(name="ArduinoSerialReaderPort2",immediate=true)
public class ArduinoSerialReaderPort2 implements SerialPortEventListener {

	
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
// 				System.out.println(spl2); 
 				
 				
 				if(spl1.equals("1")){
// 					System.out.print("Solar production: " + spl2);
 					float solarProduction=Float.valueOf(spl2.trim()).floatValue();
 					float realSolarProduction=(float) (solarProduction*0.9775);
 					solarPanel.setProsumedPower(realSolarProduction);
 				} 				
 				else if(spl1.equals("2")){
 					float power=Float.valueOf(spl2.trim()).floatValue();
 					float realPower=(float) (power/17.05);
 					lampLivingRoom1.setEmissionPower(realPower);
 					lampLivingRoom2.setEmissionPower(realPower);
 				}
 				else if(spl1.equals("3")){
 					float power=Float.valueOf(spl2.trim()).floatValue();
 					float realPower=(float) (power/17.05);
 					lampBedroom1.setEmissionPower(realPower);
 				}
 				else if(spl1.equals("4")){
 					float power=Float.valueOf(spl2.trim()).floatValue();
 					float realPower=(float) (power/17.05);
 					lampBedroom2.setEmissionPower(realPower);
 				}
 				else if(spl1.equals("5")){
 					float power=Float.valueOf(spl2.trim()).floatValue();
 					float realPower=(float) (power/17.05);
 					lampBedroom3.setEmissionPower(realPower);
 				}
 				else if(spl1.equals("6")){
 					float power=Float.valueOf(spl2.trim()).floatValue();
 					float realPower=(float) (power/17.05);
 					lampEntrance.setEmissionPower(realPower);
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

	
	@Property(mandatory=true)
	public String portName;
	
	@Requires(id="lamp1")
	public ArduinoLamp lampEntrance;
	
	@Requires(id="lamp2")
	public ArduinoLamp lampLivingRoom1;
	
	@Requires(id="lamp3")
	public ArduinoLamp lampLivingRoom2;
	
	@Requires(id="lamp4")
	public ArduinoLamp lampBedroom1;
	
	@Requires(id="lamp5")
	public ArduinoLamp lampBedroom2;
	
	@Requires(id="lamp6")
	public ArduinoLamp lampBedroom3;

	@Requires(id="solarPanel")
	public TunableProsumer solarPanel;

}
