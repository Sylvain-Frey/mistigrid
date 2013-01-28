package fr.tpt.s3.alba.simple.messages;

import fr.tpt.s3.alba.simple.roles.LoadBalancer;
import fr.tpt.s3.alba.simple.roles.Prosumer;
import fr.tpt.s3.cirrus.agent.R;

public class LoadBalancingOrder {
	
	public final R<LoadBalancer> loadBalancer;
	public final LoadPriority priority;
	public final R<Prosumer> prosumer;

	public LoadBalancingOrder(
			R<LoadBalancer> loadBalancer, 
			LoadPriority priority,
			R<Prosumer> prosumer
	) {
		super();
		this.loadBalancer = loadBalancer;
		this.priority = priority;
		this.prosumer = prosumer;
	}
	
}
