package fr.sylfrey.iris.jsonrpc;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.json.rpc.client.HttpJsonRpcClientTransport;
import org.json.rpc.client.JsonRpcInvoker;
import org.json.rpc.server.JsonRpcExecutor;
import org.json.rpc.server.JsonRpcServletTransport;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import fr.sylfrey.iris.NotFoundException;

@Component
@Provides
@Instantiate
public class JsonRpcProviderImpl extends HttpServlet implements JsonRpcProvider {

	@Requires
	public HttpService httpService;

	public static final String PATH_PREFIX = "jsonrpc";

	@Validate
	public void start() throws ServletException, NamespaceException, MalformedURLException {
		httpService.registerServlet("/" + PATH_PREFIX, this, null, null);
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public <S> void publish(String path, S service, Class<S> clazz) throws NamespaceException {

		if (executors.containsKey(path)) {
			throw new NamespaceException("Path " + path + " already in use.");
		}

		JsonRpcExecutor executor = new JsonRpcExecutor();
		executor.addHandler(path, service, clazz);
		executors.put(path, executor);

	}

	@Override
	public void unpublish(String path) {
		executors.remove(path);		
	}

	@SuppressWarnings("unchecked")
	@Override
	public <S> S get(String url, Class<S> clazz) throws NotFoundException {
		try {
			return invoker.get(new HttpJsonRpcClientTransport(new URL(url)), url, clazz);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new NotFoundException();
		}
	}

	@Override
	public <S> void release(S proxy) {
		for (Entry<String,JsonRpcExecutor> entry : executors.entrySet()) {
			if (entry.getValue() == proxy) executors.remove(entry.getKey());
		}
	}



	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handle(req, resp);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handle(req, resp);
	}

	private void handle(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String[] path = req.getPathInfo().split("/");
		// service name is in path[1], path[0] contains ""
		// example path: /ping

		if (path.length<2 || !executors.containsKey(path[1])) {
			resp.getWriter().write("Error 404: " + req.getPathInfo() + " not found ");
			return;
		}

		executors.get(path[1]).execute(new JsonRpcServletTransport(req, resp));		
	}



	private Map<String, JsonRpcExecutor> executors = new HashMap<String, JsonRpcExecutor>();
	private JsonRpcInvoker invoker = new JsonRpcInvoker();
	private static final long serialVersionUID = 1L;

}
