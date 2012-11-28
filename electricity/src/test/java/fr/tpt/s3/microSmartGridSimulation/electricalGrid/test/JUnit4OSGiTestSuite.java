package fr.tpt.s3.microSmartGridSimulation.electricalGrid.test;

import junit.framework.Test;

import org.apache.felix.ipojo.junit4osgi.OSGiTestSuite;
import org.osgi.framework.BundleContext;

import fr.tpt.s3.microSmartGridSimulation.electricalGrid.impl.SimpleTunableProsumer;

public class JUnit4OSGiTestSuite {

	public static Test suite(BundleContext bc) {
		OSGiTestSuite suite = new OSGiTestSuite("Electrical grid test suite", bc);
//		suite.addTestSuite(AggregatorTest.class);
		suite.addTestSuite(SimpleTunableProsumer.class);
		suite.addTestSuite(StorageTest.class);
		return suite;
	}

}
