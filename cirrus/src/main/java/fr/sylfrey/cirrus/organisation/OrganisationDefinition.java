package fr.sylfrey.cirrus.organisation;

import java.util.Map;

public interface OrganisationDefinition<Role> {
	
	public Map<Role, Cardinality> definition(); 

}
