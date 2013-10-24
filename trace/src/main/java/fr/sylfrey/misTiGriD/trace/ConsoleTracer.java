/*******************************************************************************
 * Copyright (c) 2013 Sylvain Frey.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Sylvain Frey - initial API and implementation
 ******************************************************************************/
package fr.sylfrey.misTiGriD.trace;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;

/**
 * A very simple Tracer service implementation, that traces everything to the standard output.
 * @author syl
 *
 */
@Component(name="ConsoleTracer")
@Provides
public class ConsoleTracer implements Tracer {

	@Override
	public void logMessage(String topic, String message) {
		if (doTrace) { System.out.println("# /" + topic + " ~> " + message); }
	}

	@Override
	public void logValue(String name, float value) {
		if (doTrace) { System.out.println("# /" + name + " ~> " + value); }
	}
		
	/**
	 * Whether or not this logger should print on the standard output.
	 */
	@Property
	public boolean doTrace;
	
	public ConsoleTracer(boolean doTrace) {
		this.doTrace = doTrace;
	}

	@Override
	public void createValueLog(String name) {
		if (doTrace) { System.out.println("# /" + name + " declared"); }
	}

	@Override
	public void createMessageLog(String topic) {
		if (doTrace) { System.out.println("# /" + topic + " declared"); }
	}

}
