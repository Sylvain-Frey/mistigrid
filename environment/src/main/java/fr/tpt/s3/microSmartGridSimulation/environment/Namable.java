package fr.tpt.s3.microSmartGridSimulation.environment;

/**
 * A service for naming components of the simulation.
 * @author syl
 *
 */
public interface Namable {

	/**
	 * @return the name of this component - a priori not unique.
	 */
	public String getName();
	
}
