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
package fr.sylfrey.misTiGriD.trace.framework.impl;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Validate;

import fr.sylfrey.misTiGriD.trace.framework.ActorContainer;
import fr.sylfrey.misTiGriD.trace.framework.topic.Topic;


@Component(name="TopicTest", immediate=true)
public class TopicTest implements Runnable {

	@Validate
	public void start() {
		running = true;
		new Thread(this).start();
	}

	@Invalidate
	public void stop() {
		running = false;
	}

	@Bind(aggregate=true,id="topic")
	public void bindTopic(ActorContainer<Topic<String>> container) {
		topics.add(container.actor());
	}
	
	public ConcurrentLinkedQueue<Topic<String>> topics = new ConcurrentLinkedQueue<Topic<String>>();

	private boolean running = true;

	@Override
	public void run() {
		while (running) {
			for (Topic<String> topic : topics) {
				System.out.println("# telling " + topic.toString());
				topic.tell("plop");
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
	}

}
