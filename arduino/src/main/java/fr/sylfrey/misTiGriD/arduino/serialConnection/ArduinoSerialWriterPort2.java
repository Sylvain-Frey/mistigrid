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
package fr.sylfrey.misTiGriD.arduino.serialConnection;

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

