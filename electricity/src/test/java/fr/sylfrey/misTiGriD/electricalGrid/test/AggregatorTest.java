package fr.sylfrey.misTiGriD.electricalGrid.test;

import org.apache.felix.ipojo.junit4osgi.OSGiTestCase;

//import akka.actor.ActorSystem;
//import akka.actor.TypedActor;
//import akka.actor.TypedProps;
//import akka.japi.Creator;
//
//import fr.tpt.s3.microSmartGridSimulation.electricalGrid.Aggregator;
import fr.sylfrey.misTiGriD.electricalGrid.BlackOut;

public class AggregatorTest extends OSGiTestCase {

	public void test() throws BlackOut {
		
//		ActorSystem actorSystem = ActorSystem.create("grid");
//				
//		SimpleTunableProsumer p1 = new SimpleTunableProsumer(); p1.name = "p1";
//		SimpleTunableProsumer p2 = new SimpleTunableProsumer(); p2.name = "p2"; p2.prosumedPower = -10f;
//		SimpleTunableProsumer p3 = new SimpleTunableProsumer(); p3.name = "p3";
//		
//		Aggregator ha = TypedActor.get(actorSystem).typedActorOf(
//				new TypedProps<AggregatorImpl>(
//						Aggregator.class, 
//						new Creator<AggregatorImpl>() {
//							public AggregatorImpl create() {
//								return new AggregatorImpl(null,"aggregator");
//							}}),
//							"aggregator");
//		assertEquals(ha.getProsumedPower(),0f);
//		assertEquals(ha.getAggregatedPowerConsumption(),0f);
//		assertEquals(ha.getAggregatedPowerProduction(),0f);
//		
//		assertEquals(p1.getProsumedPower(), 0f);
//		p1.connectTo(ha);
//		assertEquals(ha.getProsumedPower(),0f);
//		assertEquals(ha.getAggregatedPowerConsumption(),0f);
//		assertEquals(ha.getAggregatedPowerProduction(),0f);
//		
//		
//		
//		p1.setProsumedPower(10);
//		checkAgg(ha, 10f, 0f, 10f);
//		
//		p1.setProsumedPower(-100);
//		checkAgg(ha, -100f, 100f, 0f);
//		
//		ha.updateProsumption(p2, 10);
//		checkAgg(ha, -100f, 100f, 0f);
//		
//		p2.connectTo(ha);
//		checkAgg(ha, -110f, 110f, 0f);
//
//		p3.connectTo(ha);
//		p3.setProsumedPower(20);
//		checkAgg(ha, -90f, 110f, 20f);
//		
//		
//		ha.blackout();
//		checkAgg(ha, 0f, 0f, 0f);
//		assertEquals(p1.getProsumedPower(),0f,Double.MIN_VALUE);
//		assertEquals(p2.getProsumedPower(),0f,Double.MIN_VALUE);
//		assertEquals(p3.getProsumedPower(),0f,Double.MIN_VALUE);
//		
//		
//		p1.setProsumedPower(-100);
//		p2.setProsumedPower(-40);
//		p3.setProsumedPower(20);
//		checkAgg(ha, -120f, 140f, 20f);
//		
//		p2.disconnectFrom(ha);
//		checkAgg(ha, -80f, 100f, 20f);
//		
//		p1.disconnectFrom(ha);
//		checkAgg(ha, 20f, 0f, 20f);
//		
//		p3.disconnectFrom(ha);
//		checkAgg(ha, 0f, 0f, 0f);
		
	}
	
//	private void checkAgg(Aggregator a, 
//			float prosumedPower, 
//			float powerConsumption, 
//			float powerProduction) {
//		
//		assertEquals(a.getProsumedPower(),prosumedPower);
//		assertEquals(a.getAggregatedPowerConsumption(),powerConsumption);
//		assertEquals(a.getAggregatedPowerProduction(),powerProduction);	
//		
//	}

}
