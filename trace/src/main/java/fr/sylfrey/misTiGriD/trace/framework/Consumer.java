package fr.sylfrey.misTiGriD.trace.framework;

public interface Consumer<Msg> {

	public void tell(Msg msg);
	
}
