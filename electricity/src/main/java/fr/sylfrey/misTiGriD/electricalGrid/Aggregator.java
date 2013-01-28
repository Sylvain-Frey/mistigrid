package fr.sylfrey.misTiGriD.electricalGrid;


/**
 * Standard interface for grid aggregator services;
 * a grid aggregator gets updated continuously by its prosuming children,
 * cumulating all their prosumptions and prosuming the total
 * from its own aggregator father.
 * @author syl
 *
 */
public interface Aggregator extends Prosumer {
	
	public String getName();
	public void connect(Prosumer prosumer);
	public void disconnect(Prosumer prosumer);
	public void updateProsumption(Prosumer prosumer, float prosumption) throws BlackOut;
	
	public float getAggregatedPowerConsumption();
	public float getAggregatedPowerProduction();
	public float getBill();
	
}
