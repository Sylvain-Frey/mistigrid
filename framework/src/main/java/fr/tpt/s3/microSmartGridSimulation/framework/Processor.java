package fr.tpt.s3.microSmartGridSimulation.framework;

public interface Processor<MsgIn,MsgOut> {

	public MsgOut ask(MsgIn message);
	
}
