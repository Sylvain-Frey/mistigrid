package fr.sylfrey.iris.webSockets;

import static org.jboss.netty.channel.Channels.pipeline;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketVersion;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.NamespaceException;

import fr.sylfrey.iris.NotFoundException;

@Component
@Provides
//@Instantiate
public class WebSocketProviderImpl implements WebSocketProvider {

	public WebSocketProviderImpl(BundleContext context) {
		this.context = context;
	}

	@Validate
	public void start() {

		// server bootstrap
		serverBootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

		serverBootstrap.setPipelineFactory(new ServerPipelineFactory());

		String configuredPort = context.getProperty("iris.websocket.port");
		if (configuredPort != null && !configuredPort.isEmpty()) {
			this.port = Integer.parseInt(configuredPort);
		}

		serverBootstrap.bind(new InetSocketAddress(port));
		System.out.println("# WebSocket server started listening on port " + port);

		//client bootstrap
		clientBootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
				Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

		clientBootstrap.setPipelineFactory(new ClientPipelineFactory());

	}

	@Invalidate
	public void stop() {
		handlers.clear();
		openHandshakers.clear();
		serverBootstrap = null;
		clientBootstrap = null;
	}

	@Override
	public void publish(String path, WebSocketHandler handler) throws NamespaceException {
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		if (handlers.containsKey(path)) {
			throw new NamespaceException("Path " + path + " already in use.");
		}
		handlers.put(path,handler);
	}

	@Override
	public void unpublish(String path) {		
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		handlers.remove(path);
	}

	@Override
	public Channel get(String url, WebSocketHandler clientHandler) throws NotFoundException {
		try {
			URI uri = new URI(url);
			String protocol = uri.getScheme();
			if (!protocol.equals("ws")) {
				throw new IllegalArgumentException("Unsupported protocol: " + protocol);
			}

			ChannelFuture future = clientBootstrap.connect(
					new InetSocketAddress(uri.getHost(), uri.getPort()));
			future.syncUninterruptibly();
			Channel channel = future.getChannel();

			WebSocketClientHandshaker handshaker = clientHandshakerFactory.newHandshaker(
					uri, WebSocketVersion.V13, null, false, null);		
			handshaker.handshake(channel).syncUninterruptibly();

			openHandshakers.put(channel, handshaker);
			openClientHandlers.put(channel, clientHandler);
			return 	channel;
		} catch (Exception e) {
			e.printStackTrace();
			throw new NotFoundException();
		}

	}

	@Override
	public void release(Channel channel) {
		openHandshakers.remove(channel);
		openClientHandlers.remove(channel);
		channel.close();
		clientBootstrap.releaseExternalResources();
	}

	private BundleContext context;
	private ServerBootstrap serverBootstrap;
	private ClientBootstrap clientBootstrap;
	private int port = 8081;
	private Map<String,WebSocketHandler> handlers = new ConcurrentHashMap<String, WebSocketHandler>();
	private Map<Channel,WebSocketClientHandshaker> openHandshakers = new ConcurrentHashMap<Channel,WebSocketClientHandshaker>();
	private Map<Channel,WebSocketHandler> openClientHandlers = new ConcurrentHashMap<Channel,WebSocketHandler>();
	private WebSocketClientHandshakerFactory clientHandshakerFactory= new WebSocketClientHandshakerFactory();

	class ServerPipelineFactory implements ChannelPipelineFactory {
		public ChannelPipeline getPipeline() throws Exception {
			ChannelPipeline pipeline = pipeline();
			pipeline.addLast("decoder", new HttpRequestDecoder());
			pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
			pipeline.addLast("encoder", new HttpResponseEncoder());
			pipeline.addLast("ws-server-wrapper", new ServerHandlerWrapper(handlers));
			return pipeline;
		}
	}

	class ClientPipelineFactory implements ChannelPipelineFactory {
		public ChannelPipeline getPipeline() throws Exception {
			ChannelPipeline pipeline = Channels.pipeline();
			pipeline.addLast("decoder", new HttpResponseDecoder());
			pipeline.addLast("encoder", new HttpRequestEncoder());
			pipeline.addLast("ws-client-wrapper", new ClientHandlerWrapper(openHandshakers, openClientHandlers));
			return pipeline;
		}
	}

}
