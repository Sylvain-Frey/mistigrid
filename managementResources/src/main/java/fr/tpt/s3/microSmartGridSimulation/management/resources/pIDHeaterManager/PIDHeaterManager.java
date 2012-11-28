package fr.tpt.s3.microSmartGridSimulation.management.resources.pIDHeaterManager;

import fr.tpt.s3.microSmartGridSimulation.framework.Consumer;

public interface PIDHeaterManager<Message> extends Consumer<Message> {

	public float getRequiredTemperature();
	public void setRequiredTemperature(float requiredTemperature);
	public void tell(Message message);	
	
}
