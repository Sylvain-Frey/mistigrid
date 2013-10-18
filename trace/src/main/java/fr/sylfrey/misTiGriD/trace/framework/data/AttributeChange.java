package fr.sylfrey.misTiGriD.trace.framework.data;

import java.io.Serializable;

import akka.actor.ActorPath;

public class AttributeChange<T> extends Message implements Serializable {
	
	public AttributeChange(ActorPath source, T oldValue, T newValue) {
		super(source);
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	public final T oldValue;
	public final T newValue;
		
	@Override
	public String toString() {
		return "AttributeChange [source=" + source 
				+ ", oldValue=" + oldValue 
				+ ", newValue="	+ newValue + "]";
	}

	private static final long serialVersionUID = 1L;

}
