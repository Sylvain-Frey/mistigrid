package fr.tpt.s3.microSmartGridSimulation.management.touchpoints;

import java.lang.management.ManagementFactory;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class LocalJMXResource {

	public static <Type> Type proxy(String jmxDomain, String jmxType, String jmxName, Class<Type> clazz) {
		try {
			return JMX.newMBeanProxy(mbs,
					new ObjectName(jmxDomain + ":type=" + jmxType + ",instance=" + jmxName),
					clazz);
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
	
}
