package fr.sylfrey.misTiGriD.electricalGrid;

import akka.actor.ActorRef;

/**
 * Specific interface for remote aggregator, 
 * same functionality as Aggregator but with serialisable ActorRefs. 
 * @author syl
 *
 */
public interface RemoteAggregator extends Prosumer {
	
	public String getName();
	public void connect(ActorRef prosumer);
	public void disconnect(ActorRef prosumer);
	public void updateProsumption(ActorRef prosumer, Float prosumption) throws BlackOut;
	
	public float getAggregatedPowerConsumption();
	public float getAggregatedPowerProduction();
	public float getBill();
	
}
