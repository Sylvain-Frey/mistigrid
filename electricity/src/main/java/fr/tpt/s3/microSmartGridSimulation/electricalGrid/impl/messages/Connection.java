package fr.tpt.s3.microSmartGridSimulation.electricalGrid.impl.messages;

import java.io.Serializable;

public class Connection implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public final String sender;

	public Connection(String sender) {
		super();
		this.sender = sender;
	}

}
