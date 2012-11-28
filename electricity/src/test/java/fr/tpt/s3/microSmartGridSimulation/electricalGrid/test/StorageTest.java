package fr.tpt.s3.microSmartGridSimulation.electricalGrid.test;

import static fr.tpt.s3.microSmartGridSimulation.electricalGrid.Storage.State.LOADING;
import static fr.tpt.s3.microSmartGridSimulation.electricalGrid.Storage.State.UNLOADING;

import org.apache.felix.ipojo.junit4osgi.OSGiTestCase;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import fr.tpt.s3.microSmartGridSimulation.electricalGrid.impl.SimpleStorage;
import fr.tpt.s3.microSmartGridSimulation.environment.Time;

public class StorageTest extends OSGiTestCase {

	private SimpleStorage storage = new SimpleStorage();
	private Mockery context = new Mockery();
	private final Time time = context.mock(Time.class);
	
	@Test
	public void test() {
		
		storage.time = time;
		storage.name = "testStorage";
		storage.start();
		
		assertEquals(storage.name,"testStorage");
    	assertEquals(storage.getProsumedPower(),0f,Double.MIN_VALUE);
    	assertEquals(storage.getLoad(),0f,Double.MIN_VALUE);
    	

    	context.checking(new Expectations() {{
		    allowing (time).dayTime(); will(returnValue(System.currentTimeMillis()));
		}});
		
    	
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

			float load = storage.getLoad();

			assertTrue(storage.MAX_LOAD < load);
			assertTrue(load < 0);

			System.out.println("# storage state = " + storage.getState() + " : "
					+ storage.MAX_LOAD + " < " + load + " < " + 0);

		}
		
	}

}
