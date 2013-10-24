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
package fr.sylfrey.misTiGriD.trace.framework.topic;

import java.util.LinkedList;
import java.util.List;

import fr.sylfrey.misTiGriD.trace.framework.Consumer;

public class TopicImpl<Msg> implements Topic<Msg> {

	@Override
	public void subscribe(Consumer<Msg> subscriber) {
		if (subscriber==null) { return; }
		subscribers.add(subscriber);
	}

	@Override
	public void unsubscribe(Consumer<Msg> subscriber) {
		if (subscriber==null) { return; }
		subscribers.remove(subscriber);
	}

	@Override
	public void tell(Msg message) {
		for (Consumer<Msg> subscriber : subscribers) {
			subscriber.tell(message);
//			System.out.println("# topic told " + subscriber.toString());
		}		
	}
	
	private List<Consumer<Msg>> subscribers = new LinkedList<Consumer<Msg>>();

}
