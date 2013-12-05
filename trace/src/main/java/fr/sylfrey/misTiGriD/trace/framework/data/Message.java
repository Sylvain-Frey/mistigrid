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
package fr.sylfrey.misTiGriD.trace.framework.data;

import java.io.Serializable;

import akka.actor.ActorPath;

public class Message implements Serializable {
	
	public final ActorPath source;

	@Override
	public String toString() {
		return "Message [source=" + source + "]";
	}

	public Message(ActorPath source) {
		super();
		this.source = source;
	}
	
	private static final long serialVersionUID = 1L;

}
