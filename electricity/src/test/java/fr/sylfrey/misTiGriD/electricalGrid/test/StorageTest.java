/*******************************************************************************
 * Copyright (c) 2013 EDF. This software was developed with the 
 * collaboration of Télécom ParisTech (Sylvain Frey).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Sylvain Frey - initial API and implementation
 ******************************************************************************/
package fr.sylfrey.misTiGriD.electricalGrid.test;

import static fr.sylfrey.misTiGriD.electricalGrid.Storage.State.LOADING;
import static fr.sylfrey.misTiGriD.electricalGrid.Storage.State.UNLOADING;

import org.apache.felix.ipojo.junit4osgi.OSGiTestCase;
import org.junit.Test;

import fr.sylfrey.misTiGriD.electricalGrid.impl.SimpleStorage;
import fr.sylfrey.misTiGriD.environment.Time;

public class StorageTest extends OSGiTestCase {

	private SimpleStorage storage = new SimpleStorage();
	private final Time time = new MockTime();
	
	@Test
	public void test() {
		
		storage.time = time;
		storage.name = "testStorage";
		storage.MAX_LOAD = -1000; //you'll want rather -10000 or so
		storage.MAX_POWER_IN = -500;
		storage.MAX_POWER_OUT = 500;
		
		
    	storage.start();
		System.out.println("# maxLoad=" + storage.MAX_LOAD + " maxIn=" + storage.MAX_POWER_IN + " maxOut=" + storage.MAX_POWER_OUT);
		
		assertEquals(storage.name,"testStorage");
    	assertEquals(storage.getProsumedPower(),0f,Double.MIN_VALUE);
    	assertEquals(storage.getLoad(),0f,Double.MIN_VALUE);
    	

    	
    	storage.setState(LOADING);
    	waitForStabilisation();
    	
    	System.out.println("# battery loaded");
    	
    	try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
    	storage.setState(UNLOADING);
    	waitForStabilisation();

    	System.out.println("# battery unloaded");    
    	
    	storage.stop();
    	
	}


	private void waitForStabilisation() {

		while(storage.getProsumedPower()!=0) {

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			storage.update();
			float load = storage.getLoad();
			System.out.println("# storage state=" + storage.getState() 
					+ " : prosumption=" + storage.getProsumedPower() + " : "
					+ storage.MAX_LOAD + " < " + load + " < " + 0);

			assertTrue(storage.MAX_LOAD <= load);
			assertTrue(load <= 0);

		}
		
	}

	private class MockTime implements Time {

		@Override
		public long dayTime() {
			return System.currentTimeMillis();
		}

		@Override
		public long dayLength() {
			return 240;
		}
		
	}
	
}
