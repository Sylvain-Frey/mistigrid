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
package fr.sylfrey.misTiGriD.trace;

import fr.sylfrey.misTiGriD.trace.framework.Consumer;
import fr.sylfrey.misTiGriD.trace.framework.data.Message;

public class TopicTracerImpl implements Consumer<Object> {

	@Override
	public void tell(Object message) {
		logger.logMessage(
				((Message) message).source.toString(), 
				message.toString());
	}

	public Tracer logger;

	public TopicTracerImpl(Tracer logger) {
		super();
		this.logger = logger;
	}
	
}
