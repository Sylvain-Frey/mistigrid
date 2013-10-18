package fr.sylfrey.misTiGriD.trace.framework;

public interface Processor<MsgIn,MsgOut> {

	public MsgOut ask(MsgIn message);
	
}
