package fr.tpt.s3.microSmartGridSimulation.management.touchpoints;

import java.io.IOException;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * This class creates a JMX proxy with a remote T (MBean).
 * @author ada, syl
 */
public class JMXProxyLink<T> {

	public JMXProxyLink(JMXReference<T> reference) {		
		this.ref = reference;		
		start();
	}

	public T proxy() {
		return proxy;
	}

	public void start() {

		System.out.println("# JMXAdapter " + this.getClass().getSimpleName() + " starting...");

		connectToRemoteJMXServer();
		generateMBeanProxy();		

	}

	public void stop() {     

		System.out.println("# JMXCollector" + this.getClass().getSimpleName() + " stopping...");

		mbsc = null;
		if( null != jmxc ){
			try {
				jmxc.close();
				jmxc = null;//new ada
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}	

	private void connectToRemoteJMXServer() {

		if( ref.jmxHost==null || ref.jmxPort==null ) {
			System.out.println("# Warning:: in JMXProxyLink.connectToRemoteJMXServer(): " +
					"cannot connect - null parameters: host = " + ref.jmxHost 
					+ "; port = " + ref.jmxPort );
			return;
		}

		JMXServiceURL url = null;
		try {
			url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + 
					ref.jmxHost + ":"+ ref.jmxPort + "/jmxrmi");
			System.out.println("# JMXCollector: trying to connect to url: " + url );
			jmxc = JMXConnectorFactory.connect(url, null);
			System.out.println("# JMXCollector: sucessfully connected, getting the Remote MBean Server.." );
			mbsc = jmxc.getMBeanServerConnection();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	private void generateMBeanProxy() {

		if( mbsc==null ) {
			System.out.println("# JMXProxyLink.generateMBeanProxy(): " +
					"unable to get Remote MBean Server... " +
					"=> will do nothing else :)" );
			return;
		}

		if( (null == ref.jmxDomain) || (null == ref.jmxType)){
			System.out.println("# WARNING JMXProxyLink.generateMBeanProxy():" +
					"cannot create MBeanProxy for null domain and/or type: " +
					"m_jmx_domain: " + ref.jmxDomain + ", m_jmx_type: " 
					+ ref.jmxType + " => will return..");
			return;
		}
		
		System.out.println("# JMXProxyLink.generateMBeanProxy(): " +
					"domain: " + ref.jmxDomain + "; type: " + ref.jmxType );


		try {
			ObjectName mbeanName = new ObjectName(ref.jmxDomain + ":type=" + ref.jmxType);
			this.proxy = (T) JMX.newMBeanProxy(mbsc, mbeanName, ref.clazz, true);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}

	private JMXReference<T> ref;

	private JMXConnector jmxc = null;
	private MBeanServerConnection mbsc = null;

	private T proxy = null;

}
