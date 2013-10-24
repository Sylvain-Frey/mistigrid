/*******************************************************************************
 * Copyright (c) 2013 Sylvain Frey.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Sylvain Frey - initial API and implementation
 ******************************************************************************/
package fr.sylfrey.misTiGriD.webGUI;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.osgi.service.http.HttpService;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;
import akka.event.EventBus;
import fr.sylfrey.misTiGriD.trace.Archiver;
import fr.sylfrey.misTiGriD.trace.ArchiverEvent;
import fr.sylfrey.misTiGriD.wrappers.ActorSystemProvider;

@Component(name = "Traces", immediate = true)
@Provides
//@Instantiate
public class TracesImpl extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Requires
	public HttpService httpService;

	@Bind
	public void bindActorSystem(ActorSystemProvider actorSystemProvider) {
		this.actorSystem = actorSystemProvider.getSystem();
	}

	private ActorSystem actorSystem;

	@Bind
	public void bindArchiver(Archiver archiver) {
		this.bus = archiver.bus();
	}

	private EventBus bus;

	@Validate
	public void start() throws Exception {
		httpService.registerServlet("/traces", this, null, null);
		subscriber = actorSystem.actorOf(new Props(new UntypedActorFactory() {
			private static final long serialVersionUID = 1L;

			public ArchiveSubscriber create() {
				return new ArchiveSubscriber(currentState);
			}
		}));
		bus.subscribe(subscriber, ArchiverEvent.class);
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		ArrayNode data = mapper.createArrayNode();
		for (String type : currentState.keySet()) {
			ObjectNode datum = mapper.createObjectNode();
			datum.put("type", type);
			Object content = currentState.get(type);
			if (content instanceof Float) {
				datum.put("content", (Float) content);
			} else {
				datum.put("content", content.toString());
			}
			data.add(datum);
		}

		resp.getWriter().write(data.toString());

	}

	@Invalidate
	public void stop() {
		bus.unsubscribe(subscriber);
		actorSystem.stop(subscriber);
	}

	private ConcurrentHashMap<String, Object> currentState = new ConcurrentHashMap<String, Object>();

	private ActorRef subscriber;
	private ObjectMapper mapper = new ObjectMapper();

}
