package fr.tpt.s3.iris.webSockets;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.osgi.service.http.NamespaceException;

@Component(immediate=true)
//@Instantiate
public class EchoHandler implements WebSocketHandler {
	
	@Requires
	public WebSocketProvider provider;
	
	@Validate
	public void start() throws NamespaceException {
		provider.publish("/echo", this);
	}

	@Override
	public void handleWebSocketFrame(ChannelHandlerContext ctx,
			WebSocketFrame frame) {
		String answer = ((TextWebSocketFrame) frame).getText().toUpperCase();
		ctx.getChannel().write(new TextWebSocketFrame(answer));
	}

}
