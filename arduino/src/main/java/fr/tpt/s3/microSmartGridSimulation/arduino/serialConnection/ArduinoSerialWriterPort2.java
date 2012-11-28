package fr.tpt.s3.microSmartGridSimulation.arduino.serialConnection;

import java.io.IOException;

import org.apache.felix.ipojo.annotations.Component;

/**
 * Component responsible for sending data to Arduino. Connected to port 2.
 * @author dragutin
 *
 */
@Component(name="ArduinoSerialWriterPort2",immediate=true)
public class ArduinoSerialWriterPort2 implements Runnable{
	
	
     public ArduinoSerialWriterPort2 (int code)
     {
         this.code = code;
     }
     
     
     @Override
     public void run ()
     {
    	 try {
			ArduinoSerialReaderPort2.getOut().write(code);
		} catch (IOException e) {
			e.printStackTrace();
		}   	 
    	 
     }            

     private int code;
}

