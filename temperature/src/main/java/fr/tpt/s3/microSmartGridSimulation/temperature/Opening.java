package fr.tpt.s3.microSmartGridSimulation.temperature;

/**
 * A controllable opening that can be open or closed.
 * @author syl
 *
 */
public interface Opening {
	
	public void open();
	public void close();
	
	public boolean isOpen();
	public boolean isClosed();

}
