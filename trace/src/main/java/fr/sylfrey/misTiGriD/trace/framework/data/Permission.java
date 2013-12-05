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

public class Permission<Action> extends Message implements Serializable {

	public final boolean isGranted;
	public final PermissionRequest<Action> request;	

	public Permission(ActorPath source, PermissionRequest<Action> request, boolean isGranted) {
		super(source);
		this.isGranted = isGranted;
		this.request = request;
	}
	
	@Override
	public String toString() {
		return "Permission [sender=" + source 
				+ ", request=" + request.toString() 
				+ ", isGranted=" + isGranted + "]";
	}

	private static final long serialVersionUID = 1L;
	
}
