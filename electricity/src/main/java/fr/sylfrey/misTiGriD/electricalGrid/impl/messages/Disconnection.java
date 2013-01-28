package fr.sylfrey.misTiGriD.electricalGrid.impl.messages;

import java.io.Serializable;

public class Disconnection implements Serializable {

	private static final long serialVersionUID = 1L;

	public final String sender;

	public Disconnection(String sender) {
		super();
		this.sender = sender;
	}
}
