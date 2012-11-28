package fr.tpt.s3.microSmartGridSimulation.framework;

public interface Consumer<Msg> {

	public void tell(Msg msg);
	
}
