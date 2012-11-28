package fr.tpt.s3.iris.webSockets;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;

import fr.tpt.s3.iris.NotFoundException;

@Component(immediate=true)
//@Instantiate
public class EchoClient implements WebSocketHandler {

	@Requires
	public WebSocketProvider provider;
	
	private Channel channel;
	private int counter = 0;
	
	@Validate
	public void start() throws Exception {
		//let time for the echo service to be published
		new Thread(new Runnable() { public void run() {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				channel = provider.get("ws://localhost:8083/echo", EchoClient.this);
				channel.write(new TextWebSocketFrame("# yo"));
			} catch (NotFoundException e) {
				e.printStackTrace();
			}			
		}}).start();
	}

	@Override
	public void handleWebSocketFrame(ChannelHandlerContext ctx,
			WebSocketFrame frame) {
		String serverMsg = ((TextWebSocketFrame) frame).getText(); 
		System.out.println("# ws client received " + serverMsg + " from server");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String answer = serverMsg.toLowerCase() + counter++;
		channel.write(new TextWebSocketFrame(answer));
	}
	

}
