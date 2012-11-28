package fr.tpt.s3.iris.webSockets;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;

public interface WebSocketHandler {
	
	public void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame);

}
