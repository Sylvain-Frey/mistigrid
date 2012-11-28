package fr.tpt.s3.microSmartGridSimulation.management.resources.simpleRoomMonitor;

import akka.actor.ActorPath;
import akka.actor.TypedActor;
import fr.tpt.s3.microSmartGridSimulation.framework.topic.Topic;
import fr.tpt.s3.microSmartGridSimulation.management.data.TemperatureChange;
import fr.tpt.s3.microSmartGridSimulation.temperature.ThermicObject;

public class SimpleRoomMonitorImpl implements SimpleRoomMonitor {

	public ThermicObject room;
	public Topic<TemperatureChange> topic;
	public ActorPath selfPath;
	
	public SimpleRoomMonitorImpl(ThermicObject room,
			Topic<TemperatureChange> topic) {
		super();
		this.room = room;
		this.topic = topic;
		this.selfPath = TypedActor.context().self().path();
	}
	
	@Override
	public void update() {
		if (room == null) { return; }

		float oldCurrentTemperature = currentTemperature; 
		currentTemperature = room.getCurrentTemperature();
//		System.out.println("# SimpleRoomMonitorImpl updated " + oldCurrentTemperature + " ~> " + currentTemperature);

		if (oldCurrentTemperature!=currentTemperature) {
			topic.tell(new TemperatureChange(selfPath, oldCurrentTemperature, currentTemperature));
		}
	}
	
	
	private float currentTemperature = 20;	

}
