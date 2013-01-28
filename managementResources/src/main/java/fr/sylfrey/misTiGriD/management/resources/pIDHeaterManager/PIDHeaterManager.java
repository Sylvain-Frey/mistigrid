package fr.sylfrey.misTiGriD.management.resources.pIDHeaterManager;

import fr.sylfrey.misTiGriD.framework.Consumer;

public interface PIDHeaterManager<Message> extends Consumer<Message> {

	public float getRequiredTemperature();
	public void setRequiredTemperature(float requiredTemperature);
	public void tell(Message message);	
	
}
