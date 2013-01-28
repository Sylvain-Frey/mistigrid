package fr.sylfrey.cirrus.test.resource;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.BundleContext;

import fr.sylfrey.cirrus.resource.SimpleBean;
import fr.sylfrey.cirrus.runtime.SimplePersister;

@Component(immediate=true)
//@Instantiate
public class SimplePersisterTest implements Runnable {

	private BundleContext context;

	@Requires public SimplePersister persister;

	public SimplePersisterTest(BundleContext context) {
		this.context = context;
	}

	@Validate
	public void start() {
		new Thread(this).start();
	}

	SimpleBeanImpl bean1 = new SimpleBeanImpl(); //simple bean
	SimpleBeanImpl bean2 = new SimpleBeanImpl(); //another simple bean to be updated
	SimpleBeanImpl bean3 = new SimpleBeanImpl(); // should fail with no instance name
	SimpleBeanImpl bean4 = new SimpleBeanImpl(); // constructed from preset config file

	Map<String, String> config1 = new LinkedHashMap<String, String>();
	Map<String, String> config2 = new LinkedHashMap<String, String>();
	Map<String, String> config3 = new LinkedHashMap<String, String>();

	@Override
	public void run() {

		System.out.println("# testing bean persistence...");

		config1.put("instance.name", "bean1");		
		bean1.set(config1);

		config2.put("instance.name", "bean2");
		config2.put("someKey", "someValue");
		bean2.set(config2);

		bean3.set(config3);

		try {
			bean4.set(persister.getFrom(new File("beans/bean4.json")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		context.registerService(SimpleBean.class.getName(), bean1, null);
		context.registerService(SimpleBean.class.getName(), bean2, null);
		context.registerService(SimpleBean.class.getName(), bean3, null);
		context.registerService(SimpleBean.class.getName(), bean4, null);

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		config2.put("someKey", "someNewValue");
		bean2.set(config2);

		bean4.get().put("someKey", "someRedefinedValue");
		
		persister.modifiedBean(bean1);
		persister.modifiedBean(bean2);
		persister.modifiedBean(bean3);
		persister.modifiedBean(bean4);

		Map<String, String> fileConfig1 = null;
		Map<String, String> fileConfig2 = null;
		Map<String, String> fileConfig4 = null;

		try {

			fileConfig1 = persister.getFrom(new File("beans/bean1.json"));
			fileConfig2 = persister.getFrom(new File("beans/bean2.json"));
			fileConfig4 = persister.getFrom(new File("beans/bean4.json"));

		} catch (IOException e) {
			e.printStackTrace();
		}

		assertEquals(config1, fileConfig1);
		assertEquals(config2, fileConfig2);
		assertEquals(bean4.get(), fileConfig4);

		System.out.println("# end of bean persistence test");		

	}

	private void assertEquals(Map<String, String> config, Map<String, String> fileConfig) {
		assert fileConfig != null;
		assert config.size() == fileConfig.size();
		for (String key : config.keySet()) {
			System.out.println("# " + key + " : " + config.get(key) + " == " + key + " : " + fileConfig.get(key)
					+ " ?? " + config.get(key).equals(fileConfig.get(key)));
		}
	}

	class SimpleBeanImpl implements SimpleBean {

		@Override
		public Map<String, String> get() {
			return state;
		}

		@Override
		public void set(Map<String, String> state) {
			this.state = state;
		}

		private Map<String, String> state = new LinkedHashMap<String, String>();

	}

}
