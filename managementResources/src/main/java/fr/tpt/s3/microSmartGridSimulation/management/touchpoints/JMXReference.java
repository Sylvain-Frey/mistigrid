package fr.tpt.s3.microSmartGridSimulation.management.touchpoints;

/**
 * Reference on a remote JMX bean.
 * 
 * @author syl
 *
 * @param <T> MBean interface implemented by the remote bean
 */
public class JMXReference<T> {

	public String jmxHost = null;	//e.g. localhost
	public String jmxPort = null;	//e.g. 9999
	public String jmxDomain = null;	//e.g. org.tmp
	public String jmxType = null;	//e.g. Room_00, Building
	public Class<T> clazz = null;	//e.g. HeaterMBean
	
	public JMXReference(String jmxHost, String jmxPort, String jmxDomain,
			String jmxType, Class<T> clazz) {
		this.jmxHost = jmxHost;
		this.jmxPort = jmxPort;
		this.jmxDomain = jmxDomain;
		this.jmxType = jmxType;
		this.clazz = clazz;
	}
	
}
