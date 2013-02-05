package fr.sylfrey.misTiGriD.alba.basic.roles;

import fr.sylfrey.misTiGriD.alba.basic.messages.LoadBalancingOrder;
import fr.sylfrey.misTiGriD.alba.basic.messages.LoadBalancingOrderResponse;
import fr.sylfrey.misTiGriD.alba.basic.messages.ProsumerStatus;
import fr.sylfrey.misTiGriD.alba.basic.messages.Prosumption;

public interface ProsumerManager {

	public Prosumption getProsumption(); 
	public ProsumerStatus getStatus(); 
	public LoadBalancingOrderResponse tell(LoadBalancingOrder order); 
	  
}
