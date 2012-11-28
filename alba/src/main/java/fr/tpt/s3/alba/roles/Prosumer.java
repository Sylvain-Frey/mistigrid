package fr.tpt.s3.alba.roles;

import fr.tpt.s3.alba.messages.LoadBalancingOrder;
import fr.tpt.s3.cirrus.organisation.hierarchy.Subordinate;
import fr.tpt.s3.cirrus.touchpoint.Consumer;

public interface Prosumer extends Subordinate, Consumer<LoadBalancingOrder> {

}
