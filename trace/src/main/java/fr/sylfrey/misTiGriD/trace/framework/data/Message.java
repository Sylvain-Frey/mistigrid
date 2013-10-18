package fr.sylfrey.misTiGriD.trace.framework.data;

import java.io.Serializable;

import akka.actor.ActorPath;

public class Message implements Serializable {
	
	public final ActorPath source;

	@Override
	public String toString() {
		return "Message [source=" + source + "]";
	}

	public Message(ActorPath source) {
		super();
		this.source = source;
	}
	
	private static final long serialVersionUID = 1L;

}
