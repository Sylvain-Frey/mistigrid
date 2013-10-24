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

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;

import akka.actor.ActorSystem;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import fr.sylfrey.misTiGriD.trace.framework.ActorContainer;
import fr.sylfrey.misTiGriD.wrappers.ActorSystemProvider;

@Component(name="Topic",immediate=true)
@Provides(specifications={ActorContainer.class})
public class TopicContainerImpl<Message> implements ActorContainer<Topic<?>> {
	
	@Override
	public Topic<?> actor() {
		return topic;
	}
	
	@Validate
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void start() {
		topic = TypedActor.get(actorSystem).typedActorOf(
				new TypedProps<TopicImpl>(Topic.class,TopicImpl.class),
				topicPath);
	}
	
	@Invalidate
	public void stop() {
		TypedActor.get(actorSystem).stop(topic);
	}	

	@Bind
	public void bindActorSystem(ActorSystemProvider provider) {
		actorSystem = provider.getSystem();
	}
	
	@Property
	public String topicPath;
	
	public ActorSystem actorSystem;
	
	private Topic<Message> topic;
		
}
