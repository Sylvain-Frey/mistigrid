package fr.tpt.s3.microSmartGridSimulation.environment;


/**
 * Interface that any dynamic component in the simulation should implement.
 * The Updatable service should be exposed so that a simulation timer
 * can detect the component and update it periodically.
 * @author syl
 *
 */
public interface Updatable {
	
	/**
	 * The period of updates requested by this component.
	 * @return period in milliseconds
	 */
	public int getPeriod(); 
	
	/**
	 * Trigger an update of this component.
	 */
	public void update();

}
