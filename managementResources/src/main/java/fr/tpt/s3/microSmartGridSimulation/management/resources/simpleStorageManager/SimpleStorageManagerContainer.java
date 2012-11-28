package fr.tpt.s3.microSmartGridSimulation.management.resources.simpleStorageManager;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import akka.actor.ActorSystem;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import akka.japi.Creator;
import fr.tpt.s3.akka.ActorSystemProvider;
import fr.tpt.s3.microSmartGridSimulation.electricalGrid.Storage;
import fr.tpt.s3.microSmartGridSimulation.framework.ActorContainer;
import fr.tpt.s3.microSmartGridSimulation.framework.Consumer;
import fr.tpt.s3.microSmartGridSimulation.framework.topic.Topic;
import fr.tpt.s3.microSmartGridSimulation.management.data.LoadMessage;

@Component(name="SimpleStorageManager",immediate=true)
public class SimpleStorageManagerContainer {

	@Validate
	public void start() {
		manager = TypedActor.get(actorSystem).typedActorOf(
				new TypedProps<SimpleStorageManager>(
						Consumer.class, 
						new Creator<SimpleStorageManager>() {
							public SimpleStorageManager create() {
								return new SimpleStorageManager(storage);
							}}),
							actorPath);
		topic.subscribe(manager);
	}

	@Invalidate
	public void stop() {
		TypedActor.get(actorSystem).stop(manager);
	}
	
	@Bind
	public void bindActorSystem(ActorSystemProvider provider) {
		actorSystem = provider.getSystem();
	}

	public ActorSystem actorSystem;	

	@Bind(id="topic")
	public void bindTopic(ActorContainer<Topic<LoadMessage>> container) {
		topic = container.actor();
	}
	
	private Topic<LoadMessage> topic;	
	
	@Requires
	public Storage storage;

	@Property(mandatory=true)
	public String actorPath;
	
	
	private Consumer<LoadMessage> manager;
	
}
