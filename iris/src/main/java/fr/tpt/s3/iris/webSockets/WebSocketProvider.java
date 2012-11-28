package fr.tpt.s3.iris.webSockets;

import org.jboss.netty.channel.Channel;
import org.osgi.service.http.NamespaceException;

import fr.tpt.s3.iris.NotFoundException;

public interface WebSocketProvider {
	
	public void publish(String path, WebSocketHandler handler) throws NamespaceException;
	public void unpublish(String path);
	
	public Channel get(String url, WebSocketHandler clientHandler) throws NotFoundException;
	public void release(Channel channel);

}
