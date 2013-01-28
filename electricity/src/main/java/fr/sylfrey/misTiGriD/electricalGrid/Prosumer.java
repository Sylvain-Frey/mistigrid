package fr.sylfrey.misTiGriD.electricalGrid;

import fr.sylfrey.misTiGriD.environment.Namable;

/**
 * Standard interface for any electricity prosuming appliance.
 * Examples Prosumers : storage, washing machine, solar panel, aggregator.
 * @author syl
 *
 */
public interface Prosumer extends Namable {
	
	/**
	 * @return electrical power (in Watt) prosumed by this appliance.
	 */
	public float getProsumedPower();
	
	/**
	 * When the electrical networks shuts down, 
	 * every connected prosumer endures a blackout.
	 */
	public void blackout();
		
}