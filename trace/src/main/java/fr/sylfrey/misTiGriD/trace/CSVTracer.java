package fr.sylfrey.misTiGriD.trace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;

/**
 * Implementation of the Tracer service, stores data on disk via CSV files.
 * @author syl
 *
 */
@Component(name="CSVTracer")
@Provides(specifications={Tracer.class})
public class CSVTracer implements Tracer {

	@Validate
	public void start() {
		startupTime = System.currentTimeMillis();
	}
	

	@Override
	public void createValueLog(String name) {
		if (!graphLogs.containsKey(name)) {
			graphLogs.put(name,	initFile(name));
		}
	}

	@Override
	public void createMessageLog(String topic) {
		String filteredTopic = topic.replace("/", ":");
		if (!msgLogs.containsKey(filteredTopic)) {
			msgLogs.put(filteredTopic, initFile(filteredTopic));
		}
	}
	
	@Override
	public void logMessage(String topic, String message) {
		String filteredTopic = topic.replace("/", ":");
		write(msgLogs.get(filteredTopic), message);
	}

	@Override
	public void logValue(String name, float value) {
		write(graphLogs.get(name), Float.toString(value));
	}


	/**
	 * Location where logs should be store on the disk.
	 */
	@Property(mandatory=true)
	public String baseDirName; 

	private long startupTime;
	
	private Map<String,RandomAccessFile> graphLogs = new HashMap<String, RandomAccessFile>();
	private Map<String,RandomAccessFile> msgLogs = new HashMap<String, RandomAccessFile>();
		
	private RandomAccessFile initFile(String name) {		
		try {
			String fileName = pathToLogFile(name + ".log");
			File dataFile = new File(fileName);
			if (dataFile.exists()) {
				dataFile.delete();
			}
			System.out.println("# tracer created " + fileName);
			return new RandomAccessFile(fileName, "rw");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}	

	private void write(RandomAccessFile file, String data) {
		try {
			file.writeBytes( (System.currentTimeMillis()-startupTime)/1000 + "," + data + "\n" );
		} catch (Exception e) { // catching null exceptions when trying to write non-existing log
			e.printStackTrace();
		}
	}
	
	private String pathToLogFile(String fileName) {
		return baseDirName + fileSeparator + fileName;
	}
	
	private String fileSeparator = System.getProperty("file.separator");

	
}
