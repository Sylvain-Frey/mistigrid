package fr.sylfrey.misTiGriD.framework;

public interface Consumer<Msg> {

	public void tell(Msg msg);
	
}
