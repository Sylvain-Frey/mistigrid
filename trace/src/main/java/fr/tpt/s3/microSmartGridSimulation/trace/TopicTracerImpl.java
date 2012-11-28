package fr.tpt.s3.microSmartGridSimulation.trace;

import fr.tpt.s3.microSmartGridSimulation.framework.Consumer;
import fr.tpt.s3.microSmartGridSimulation.framework.data.Message;

public class TopicTracerImpl implements Consumer<Object> {

	@Override
	public void tell(Object message) {
		logger.logMessage(
				((Message) message).source.toString(), 
				message.toString());
	}

	public Tracer logger;

	public TopicTracerImpl(Tracer logger) {
		super();
		this.logger = logger;
	}
	
}
