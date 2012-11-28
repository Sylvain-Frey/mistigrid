package fr.tpt.s3.microSmartGridSimulation.management.resources.loadAnalyser;

import akka.actor.ActorPath;
import akka.actor.TypedActor;
import fr.tpt.s3.microSmartGridSimulation.electricalGrid.Prosumer;
import fr.tpt.s3.microSmartGridSimulation.framework.topic.Topic;
import fr.tpt.s3.microSmartGridSimulation.management.data.Load;
import fr.tpt.s3.microSmartGridSimulation.management.data.LoadMessage;

public class LoadAnalyserImpl implements LoadAnalyser {

	public LoadAnalyserImpl(Topic<LoadMessage> topic, Prosumer prosumer,
			float lowThreshold, float highThreshold) {
		super();
		this.topic = topic;
		this.prosumer = prosumer;
		this.lowThreshold = lowThreshold;
		this.highThreshold = highThreshold;
		this.selfPath = TypedActor.context().self().path();
	}

	@Override
	public void update() {
		float prosumption = prosumer.getProsumedPower();

		if (load!=Load.HIGH) { 
			if (prosumption<highThreshold) {
				load = Load.HIGH;
			} else if (prosumption>lowThreshold) {
				load = Load.LOW;
			} else {
				load = Load.STD;
			}
		} else {
			if (prosumption < highThreshold + 200) {
				load = Load.HIGH;
			} else {
				load = Load.STD;
			}
		}
		
		topic.tell(new LoadMessage(selfPath, load));
		
	}	


	public Topic<LoadMessage> topic;

	public Prosumer prosumer;

	public float lowThreshold; // !! probably negative !!

	public float highThreshold; // !! probably negative !!

	public ActorPath selfPath; // !! probably negative !!
	
	private Load load;

}
