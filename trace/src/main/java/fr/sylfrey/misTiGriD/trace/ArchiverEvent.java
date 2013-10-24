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

public class ArchiverEvent<Content> {
	public final String type;
	public final Content content;
	public ArchiverEvent(String type, Content content) {
		this.type = type;
		this.content = content;
	}
	@Override public String toString() {
		return "{type : " + type + ", content : " + content + "}";
	}
}
