package fr.sylfrey.misTiGriD.management.resources.simpleRoomMonitor;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import akka.actor.ActorSystem;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import akka.japi.Creator;
import fr.sylfrey.akka.ActorSystemProvider;
import fr.sylfrey.misTiGriD.environment.Updatable;
import fr.sylfrey.misTiGriD.framework.ActorContainer;
import fr.sylfrey.misTiGriD.framework.topic.Topic;
import fr.sylfrey.misTiGriD.management.data.TemperatureChange;
import fr.sylfrey.misTiGriD.temperature.ThermicObject;

@Component(name="SimpleRoomMonitor",immediate=true)
@Provides(specifications={Updatable.class})
public class SimpleRoomMonitorContainer implements Updatable {

	@Validate
	public void start() {
		 manager = TypedActor.get(actorSystem).typedActorOf(
					new TypedProps<SimpleRoomMonitorImpl>(
							SimpleRoomMonitor.class, 
							new Creator<SimpleRoomMonitorImpl>() {
								public SimpleRoomMonitorImpl create() {
									return new SimpleRoomMonitorImpl(room, topic);
								}
							}),
							actorPath);
	}
	

	@Override
	public void update() {
		manager.update();
	}

	@Override
	public int getPeriod() {
		return period;
	}	
	
	@Invalidate
	public void stop() {
		TypedActor.get(actorSystem).stop(manager);
	}

	
	@Requires(id="room")
	public ThermicObject room;

	@Bind(id="topic")
	public void bindTopic(ActorContainer<Topic<TemperatureChange>> container) {
		topic = container.actor();
	}
	
	public Topic<TemperatureChange> topic;

	@Property(mandatory=true)
	public int period;

	@Property(mandatory=true)
	public String actorPath;

	@Bind
	public void bindActorSystem(ActorSystemProvider provider) {
		actorSystem = provider.getSystem();
	}

	public ActorSystem actorSystem;	

	private SimpleRoomMonitor manager;

}