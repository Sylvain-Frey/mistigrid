package fr.sylfrey.cirrus.runtime;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Modified;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import fr.sylfrey.cirrus.resource.SimpleBean;

@Component
@Provides
@Instantiate
public class SimplePersisterImpl implements SimplePersister {
	
	@Validate
	public void start() {
		if (baseFolderName == null || baseFolderName.isEmpty() || !new File(baseFolderName).exists()) {
			System.err.println("# warning : bad SimplePersister base folder configuration " + baseFolderName);
			System.err.println("# falling back to default ~/beans");
			baseFolderName = "beans";
		}
	}
	
	@Invalidate
	public void stop() {
		for (SimpleBean bean : beans.keySet()) {
			File json = beans.get(bean);
			persist(bean, json);
		}
		beans.clear();
	}
	
	
	
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String> getFrom(File configFile)
			throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(configFile, Map.class);
	}
	
	@Override
	public void writeDown(Map<String, String> config, File configFile) 
			throws JsonGenerationException, JsonMappingException, IOException {
		mapper.writeValue(configFile, config);
	}

	
	

	@Override
	@Bind(id="beanBinding", aggregate=true, optional=true)
	public void bindBean(SimpleBean bean) {
		String id = bean.get().get("instance.name");
		if (id != null && !id.isEmpty()) {
			File json = new File(baseFolderName + File.separator + id + ".json");
			beans.put(bean, json);
			if (json!=null && json.exists()) persist(bean, json);
		} else {
			System.err.println("# warning : SimpleBean " + bean 
					+ " without instance.name will not be persisted by SimplePersister");
		}
	}

	@Override
	@Modified(id="beanBinding")
	public void modifiedBean(SimpleBean bean) {
		File json = beans.get(bean);
		if (json!=null && json.exists()) persist(bean, json);
	}	

	@Override
	@Unbind(id="beanBinding")
	public void unbindBean(SimpleBean bean) {
		File json = beans.get(bean);
		if (json!=null && json.exists()) persist(bean, json);
		beans.remove(bean);
	}
	
	
	
	private void persist(SimpleBean bean, File json) {
		try {
			mapper.writeValue(json, bean.get());
		} catch (IOException e) {
			System.err.println("# warning : SimpleBean " + bean 
					+ " with instance.name = " + bean.get().get("instance.name") + " could not be persisted by SimplePersister");
			e.printStackTrace();
		}		
	}
	
	@Property(mandatory=true, value="beans")
	public String baseFolderName;
	
	private Map<SimpleBean, File> beans = new LinkedHashMap<SimpleBean, File>();
	private ObjectMapper mapper = new ObjectMapper();
			
}
