package fr.sylfrey.cirrus.runtime;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import fr.sylfrey.cirrus.resource.SimpleBean;

public interface SimplePersister {

	public Map<String, String> getFrom(File configFile)
			throws JsonParseException, JsonMappingException, IOException;

	public void writeDown(Map<String, String> config, File configFile)
			throws JsonGenerationException, JsonMappingException, IOException;
	
	public void bindBean(SimpleBean bean);
	public void modifiedBean(SimpleBean bean);
	public void unbindBean(SimpleBean bean);

}