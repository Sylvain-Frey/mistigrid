package fr.tpt.s3.microSmartGridSimulation.arduino.serialConnection;


import java.io.IOException;
import java.util.concurrent.Semaphore;

import org.apache.felix.ipojo.annotations.Component;

import fr.tpt.s3.microSmartGridSimulation.arduino.impl.ArduinoHeaterImpl;

/**
 * Component responsible for sending data to Arduino. Connected to port 0.
 * @author dragutin
 *
 */
@Component(name="ArduinoSerialWriterPort0",immediate=true)
public class ArduinoSerialWriterPort0 implements Runnable{
	
	
     public ArduinoSerialWriterPort0 (int code)
     {
         this.code = code;
     }
          
     
     @Override
     public void run ()
     {
    	 try {
    		 
    		 sem.acquire();
    		 
        	 if(code==hBedroom1){
//        		 float power=heaterBedroom1.getEmissionPower();
        		 float power=ArduinoHeaterImpl.getpPowerBedroom1();
        		 int newPower=arduinoPowerTransform(power);
        		 ArduinoSerialReaderPort0.getOut().write(code);
        		 ArduinoSerialReaderPort0.getOut().write(newPower);
        	 }
        	 else if(code==hBedroom2){
//        		 float power=heaterBedroom2.getEmissionPower();
        		 float power=ArduinoHeaterImpl.getpPowerBedroom2();
        		 int newPower=arduinoPowerTransform(power);
        		 ArduinoSerialReaderPort0.getOut().write(code);
        		 ArduinoSerialReaderPort0.getOut().write(newPower);
        		 
        	 }
        	 else if(code==hBedroom3){
//        		 float power=heaterBedroom3.getEmissionPower();
        		 float power=ArduinoHeaterImpl.getpPowerBedroom3();
        		 int newPower=arduinoPowerTransform(power);
        		 ArduinoSerialReaderPort0.getOut().write(code);
        		 ArduinoSerialReaderPort0.getOut().write(newPower);
        	 }

        	 else if(code==hBathroom){
//        		 float power=heaterBathroom.getEmissionPower();
        		 float power=ArduinoHeaterImpl.getpPowerBathroom();
        		 int newPower=arduinoPowerTransform(power);
        		 ArduinoSerialReaderPort0.getOut().write(code);
        		 ArduinoSerialReaderPort0.getOut().write(newPower);
        	 }

        	 else if(code==hEntrance){
//        		 float power=heaterEntrance.getEmissionPower();
        		 float power=ArduinoHeaterImpl.getpPowerEntrance();
        		 int newPower=arduinoPowerTransform(power);
        		 ArduinoSerialReaderPort0.getOut().write(code);
        		 ArduinoSerialReaderPort0.getOut().write(newPower);
        	 }else{
        		 ArduinoSerialReaderPort0.getOut().write(code);
        	 }
        	 
        	 sem.release();
        	 
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
                	
//        	 if(code==7){
//        		 ArduinoSerialReaderPort0.getOut().write(code);
//        		 int power=ArduinoHeater.getArduinoProsumedPower();
//        		 ArduinoSerialReaderPort0.getOut().write(power);
//        	 }else{
//        		 ArduinoSerialReaderPort0.getOut().write(code);
//        	 }        	 
        	 
     }
     
     public int arduinoPowerTransform(float newPower){
// 		newPower*=-1;
 		newPower*=51;
 		newPower/=81;
 		if(newPower>255){
 			newPower=255;
 		}else if(newPower>0 && newPower<10){
 			newPower=10;
 		}
 		 		
 		return (int)newPower;
     }
            
     private int code;
     
     private Semaphore sem=new Semaphore(1);
     
     private final int hBedroom1=7;
     
     private final int hBedroom2=8;
     
     private final int hBedroom3=9;
     
     private final int hBathroom=10;
     
     private final int hEntrance=11;
     
//     @Property(mandatory=true)
// 	 public int hBedroom1;	
//     
//     @Property(mandatory=true)
// 	 public int hBedroom2;	
//     
//     @Property(mandatory=true)
// 	 public int hBedroom3;	
//     
//     @Property(mandatory=true)
// 	 public int hBathroom;	
//     
//     @Property(mandatory=true)
// 	 public int hEntrance;	     
     
     //THIS IS NOT USED  
//     @Requires(id="heater1")
// 	 public Heater heaterBedroom1;
//
//     @Requires(id="heater2")
// 	 public Heater heaterBedroom2;
//
//     @Requires(id="heater3")
// 	 public Heater heaterBedroom3;
//
//     @Requires(id="heater4")
// 	 public Heater heaterBathroom;
//
//     @Requires(id="heater5")
// 	 public Heater heaterEntrance;

}

