package fr.tpt.s3.iris.webSockets;

import java.util.Map;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.jboss.netty.util.CharsetUtil;

public class ClientHandlerWrapper extends SimpleChannelUpstreamHandler {

	public ClientHandlerWrapper(
			Map<Channel,WebSocketClientHandshaker> openHandshakers, 
			Map<Channel,WebSocketHandler> openClientHandlers) {
		this.openHandshakers = openHandshakers;
		this.openClientHandlers = openClientHandlers;
	}
	private Map<Channel,WebSocketClientHandshaker> openHandshakers; 
	private Map<Channel,WebSocketHandler> openClientHandlers;
	private WebSocketHandler delegateHandler;
	private WebSocketClientHandshaker handshaker;

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {

		Channel channel = ctx.getChannel();
		if (handshaker==null) handshaker = openHandshakers.get(channel);
		if (delegateHandler==null) delegateHandler = openClientHandlers.get(channel);

		if (e.getMessage() instanceof HttpResponse) {
			HttpResponse response = (HttpResponse) e.getMessage();
			if (!handshaker.isHandshakeComplete()) {
				handshaker.finishHandshake(channel, response);
				return;
			}
			throw new Exception("Unexpected HttpResponse (status=" + response.getStatus() + ", content="
					+ response.getContent().toString(CharsetUtil.UTF_8) + ")");
		}

		delegateHandler.handleWebSocketFrame(ctx, (WebSocketFrame) e.getMessage());

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		e.getCause().printStackTrace();
		e.getChannel().close();
	}

}
