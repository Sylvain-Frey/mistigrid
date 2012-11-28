package fr.tpt.s3.microSmartGridSimulation.framework.topic;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;

import akka.actor.ActorSystem;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import fr.tpt.s3.akka.ActorSystemProvider;
import fr.tpt.s3.microSmartGridSimulation.framework.ActorContainer;

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