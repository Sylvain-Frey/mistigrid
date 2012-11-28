package fr.tpt.s3.microSmartGridSimulation.management.data;

import akka.actor.ActorPath;
import fr.tpt.s3.microSmartGridSimulation.framework.data.Message;

public class LoadMessage extends Message {

	public LoadMessage(ActorPath source, Load load) {
		super(source);
		this.load = load;
	}
	
	public final Load load;
	
	@Override
	public String toString() {
		return "Message [source=" + source + ", load=" + load + "]";		
	}

	private static final long serialVersionUID = 1L;
	
}
