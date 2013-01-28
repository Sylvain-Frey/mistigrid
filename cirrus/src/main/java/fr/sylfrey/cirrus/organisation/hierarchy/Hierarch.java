package fr.sylfrey.cirrus.organisation.hierarchy;

import fr.sylfrey.cirrus.agent.R;
import fr.sylfrey.cirrus.register.Registry;

public interface Hierarch<Order> extends Registry<R<Subordinate<Order>>> {
	
}
