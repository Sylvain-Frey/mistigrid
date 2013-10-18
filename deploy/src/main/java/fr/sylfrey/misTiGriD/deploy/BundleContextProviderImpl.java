package fr.sylfrey.misTiGriD.deploy;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.osgi.framework.BundleContext;

@Component
@Provides
@Instantiate
public class BundleContextProviderImpl implements BundleContextProvider {

	public BundleContextProviderImpl(BundleContext context) {
		this.context = context;
	}
	private BundleContext context;
	
	@Override
	public BundleContext get() {
		return context;
	}

}
