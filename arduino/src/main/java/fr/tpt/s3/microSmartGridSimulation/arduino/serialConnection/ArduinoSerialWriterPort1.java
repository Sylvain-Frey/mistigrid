package fr.tpt.s3.microSmartGridSimulation.arduino.serialConnection;


import java.io.IOException;
import java.util.concurrent.Semaphore;

import org.apache.felix.ipojo.annotations.Component;

import fr.tpt.s3.microSmartGridSimulation.arduino.impl.ArduinoHeaterImpl;

/**
 * Component responsible for sending data to Arduino. Connected to port 1.
 * @author dragutin
 *
 */
@Component(name="ArduinoSerialWriterPort1",immediate=true)
public class ArduinoSerialWriterPort1 implements Runnable{
	
	
     public ArduinoSerialWriterPort1 (int code)
     {
         this.code = code;
     }
     
     
     @Override
     public void run ()
     {    	 
         try
         {          
        	 
        	 sem.acquire();
        	 
//        	 System.out.println("Look: " + code + "==" + hLivingRoom1 );
//        	 System.out.println("Look: " + code + "==" + hLivingRoom2 );
//        	 System.out.println("Look: " + code + "==" + hKitchen );
        	 if(code==hLivingRoom1){
        		 float power=ArduinoHeaterImpl.getpPowerLivingRoom1();
        		 int newPower=arduinoPowerTransform(power);
//        		 System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!" + code + " :" + newPower);
        		 ArduinoSerialReaderPort1.getOut().write(code);
        		 ArduinoSerialReaderPort1.getOut().write(newPower);
        	 }        	 
        	 else if(code==hLivingRoom2){
//        		 float power=heater_livingRoom2.getEmissionPower();
        		 float power=ArduinoHeaterImpl.getpPowerLivingRoom2();
        		 int newPower=arduinoPowerTransform(power);
//        		 System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!" + code + " :" + newPower);
        		 ArduinoSerialReaderPort1.getOut().write(code);
        		 ArduinoSerialReaderPort1.getOut().write(newPower);
        	 }
        	 else if(code==hKitchen){
//        		 float power=heater_kitchen.getEmissionPower();
        		 float power=ArduinoHeaterImpl.getpPowerKitchen();
        		 int newPower=arduinoPowerTransform(power);
//        		 System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!" + code + " :" + newPower);
        		 ArduinoSerialReaderPort1.getOut().write(code);
        		 ArduinoSerialReaderPort1.getOut().write(newPower);
        	 }
        	 else{
//        		 System.out.println("# ArduinoSerialWriterPort1.run: code = " + code);
        		 ArduinoSerialReaderPort1.getOut().write(code);
        	 }
        	 
        	 sem.release();

         }
         catch (IOException e)
         {
             e.printStackTrace();
         } catch (InterruptedException e) {
			e.printStackTrace();
		}		
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
     
     private final int hLivingRoom1=5;
     
     private final int hLivingRoom2=6;
     
     private final int hKitchen=7;
     
     
     //THIS IS NOT USED because it does not work this way. Have to use static methods 
     //to successfully communicate with heater.
//     @Requires(id="heater1")
// 	 public Heater heater_livingRoom1;
//
//     @Requires(id="heater2")
// 	 public Heater heater_livingRoom2;
//
//     @Requires(id="heater3")
// 	 public Heater heater_kitchen;
     
//   @Property(mandatory=true)
//	 public int hLivingRoom1;	
//   
//   @Property(mandatory=true)
//	 public int hLivingRoom2;	
//   
//   @Property(mandatory=true)
//	 public int hKitchen;	
     
}

