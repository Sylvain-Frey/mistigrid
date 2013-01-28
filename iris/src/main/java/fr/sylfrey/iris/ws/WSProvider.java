package fr.sylfrey.iris.ws;

import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.NamespaceException;

import fr.sylfrey.iris.IrisProvider;

@Component
@Provides
//@Instantiate
public class WSProvider implements IrisProvider {

	public WSProvider(BundleContext bc) {
		bundleContext = bc;
	}

	BundleContext bundleContext;
	
	@Override
	public <S> void publish(String url, S service, Class<S> clazz)	throws NamespaceException {
		Dictionary<String, String> props = new Hashtable<String, String>();

        props.put("service.exported.interfaces", clazz.getName());
        props.put("service.exported.configs", "org.apache.cxf.rs");
        props.put("org.apache.cxf.rs.address", "http://localhost:8081/" + url);
        
        bundleContext.registerService(clazz.getName(), service, props);
	}

	@Override
	public void unpublish(String url) {
		
	}

	@Override
	public <S> S get(String url, Class<S> clazz) {
		return null;//JAXRSClientFactory.create(url, clazz);
	}
	@Override
	public <S> void release(S proxy) {
		// TODO Auto-generated method stub
		
	}

}
