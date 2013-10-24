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
package fr.sylfrey.misTiGriD.trace.framework.data;

import java.io.Serializable;

import akka.actor.ActorPath;

public class AttributeChange<T> extends Message implements Serializable {
	
	public AttributeChange(ActorPath source, T oldValue, T newValue) {
		super(source);
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	public final T oldValue;
	public final T newValue;
		
	@Override
	public String toString() {
		return "AttributeChange [source=" + source 
				+ ", oldValue=" + oldValue 
				+ ", newValue="	+ newValue + "]";
	}

	private static final long serialVersionUID = 1L;

}
