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

import junit.framework.Test;

import org.apache.felix.ipojo.junit4osgi.OSGiTestSuite;
import org.osgi.framework.BundleContext;

import fr.sylfrey.misTiGriD.electricalGrid.impl.SimpleTunableProsumer;

public class JUnit4OSGiTestSuite {

	public static Test suite(BundleContext bc) {
		OSGiTestSuite suite = new OSGiTestSuite("Electrical grid test suite", bc);
		suite.addTestSuite(SimpleTunableProsumer.class);
		suite.addTestSuite(StorageTest.class);
		return suite;
	}

}
