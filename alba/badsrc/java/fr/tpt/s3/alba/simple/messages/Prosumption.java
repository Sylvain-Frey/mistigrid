package fr.tpt.s3.alba.simple.messages;

import java.util.Date;

import fr.tpt.s3.alba.simple.roles.Prosumer;

public class Prosumption {
	
	public final Prosumer prosumer;
	public final Float prosumption;
	public final Date date;
	
	public Prosumption(Prosumer prosumer, Float prosumption, Date date) {
		super();
		this.prosumer = prosumer;
		this.prosumption = prosumption;
		this.date = date;
	}

}
