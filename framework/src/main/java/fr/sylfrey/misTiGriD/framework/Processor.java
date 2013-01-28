package fr.sylfrey.misTiGriD.framework;

public interface Processor<MsgIn,MsgOut> {

	public MsgOut ask(MsgIn message);
	
}
