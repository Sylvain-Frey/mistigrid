package fr.sylfrey.misTiGriD.trace;

import fr.sylfrey.misTiGriD.trace.framework.Consumer;
import fr.sylfrey.misTiGriD.trace.framework.data.Message;

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
