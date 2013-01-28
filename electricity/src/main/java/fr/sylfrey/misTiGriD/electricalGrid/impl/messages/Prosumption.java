package fr.sylfrey.misTiGriD.electricalGrid.impl.messages;

import java.io.Serializable;

public class Prosumption implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public final float prosumption;
	public final String sender;
	
	public Prosumption(float prosumption, String sender) {
		this.prosumption = prosumption;
		this.sender = sender;
	}

}
