package fr.tpt.s3.microSmartGridSimulation.framework.data;

import java.io.Serializable;

import akka.actor.ActorPath;

public class PermissionRequest<Action> extends Message implements Serializable {

	public final Action action;
	
	public PermissionRequest(ActorPath author, Action action) {
		super(author);
		this.action = action;
	}
	
	@Override
	public String toString() {
		return "PermissionRequest [source=" + source 
				+ ", action=" + action.toString() + "]";
	}
	
	private static final long serialVersionUID = 1L;
	
}
