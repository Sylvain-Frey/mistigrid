package fr.sylfrey.misTiGriD.trace;

import fr.sylfrey.misTiGriD.framework.Consumer;
import fr.sylfrey.misTiGriD.framework.data.Message;

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
