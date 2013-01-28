package fr.sylfrey.misTiGriD.framework.data;

import java.io.Serializable;

import akka.actor.ActorPath;

public class Permission<Action> extends Message implements Serializable {

	public final boolean isGranted;
	public final PermissionRequest<Action> request;	

	public Permission(ActorPath source, PermissionRequest<Action> request, boolean isGranted) {
		super(source);
		this.isGranted = isGranted;
		this.request = request;
	}
	
	@Override
	public String toString() {
		return "Permission [sender=" + source 
				+ ", request=" + request.toString() 
				+ ", isGranted=" + isGranted + "]";
	}

	private static final long serialVersionUID = 1L;
	
}
