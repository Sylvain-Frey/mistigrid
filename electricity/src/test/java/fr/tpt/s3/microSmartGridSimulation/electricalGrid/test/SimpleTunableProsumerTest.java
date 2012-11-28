package fr.tpt.s3.microSmartGridSimulation.electricalGrid.test;

import org.apache.felix.ipojo.junit4osgi.OSGiTestCase;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import fr.tpt.s3.microSmartGridSimulation.electricalGrid.Aggregator;
import fr.tpt.s3.microSmartGridSimulation.electricalGrid.BlackOut;
import fr.tpt.s3.microSmartGridSimulation.electricalGrid.impl.SimpleTunableProsumer;

public class SimpleTunableProsumerTest extends OSGiTestCase {

	Mockery context = new Mockery();	
	
	@Test
	public void test() throws BlackOut {
		
		final Aggregator aggregator = context.mock(Aggregator.class);
		final SimpleTunableProsumer prosumer = new SimpleTunableProsumer();
		prosumer.name = "testProsumer";
		prosumer.prosumedPower = 10f;
		assertEquals(prosumer.getName(),"testProsumer");
		
		context.checking(new Expectations() {{
		    oneOf (aggregator).connect(prosumer);
		    oneOf (aggregator).updateProsumption(prosumer,10f);
		}});
		
		
		prosumer.connectTo(aggregator);
		
		context.assertIsSatisfied();
		assertEquals(prosumer.aggregator,aggregator);
		assertEquals(prosumer.getProsumedPower(),10f,Double.MIN_NORMAL);
		
		
		
		context.checking(new Expectations() {{
		    oneOf (aggregator).updateProsumption(prosumer,10f);
		}});
		
		prosumer.setProsumedPower(10);
		
		context.assertIsSatisfied();
		assertEquals(prosumer.getProsumedPower(),10f,Double.MIN_NORMAL);
		
		
		
		context.checking(new Expectations() {{
		    oneOf (aggregator).updateProsumption(prosumer,0f);
		}});
				
		prosumer.setProsumedPower(0);
		
		context.assertIsSatisfied();		
		assertEquals(prosumer.getProsumedPower(),0f,Double.MIN_NORMAL);
		
		
		
		context.checking(new Expectations() {{
		    oneOf (aggregator).updateProsumption(prosumer,-100f);
		}});
		
		prosumer.setProsumedPower(-100);
		
		context.assertIsSatisfied();
		assertEquals(prosumer.getProsumedPower(),-100f,Double.MIN_NORMAL);
		
		
		context.checking(new Expectations() {{
		    oneOf (aggregator).updateProsumption(prosumer,200f); 
		    will(throwException(new BlackOut()));
		}});
				
		prosumer.setProsumedPower(200);
		
		context.assertIsSatisfied();		
		assertEquals(prosumer.getProsumedPower(),0f,Double.MIN_NORMAL);

	}

}
