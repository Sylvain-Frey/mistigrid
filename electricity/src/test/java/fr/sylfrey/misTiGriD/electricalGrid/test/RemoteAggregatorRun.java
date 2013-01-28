package fr.sylfrey.misTiGriD.electricalGrid.test;

import akka.actor.ActorSystem;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import akka.japi.Creator;

import com.typesafe.config.ConfigFactory;

import fr.sylfrey.misTiGriD.electricalGrid.RemoteAggregator;
import fr.sylfrey.misTiGriD.electricalGrid.impl.RemoteAggregatorImpl;

public class RemoteAggregatorRun {
	
	public static void main(String[] args) throws InterruptedException {
		RemoteAggregator gridAggregator = TypedActor.get(system).typedActorOf(
				new TypedProps<RemoteAggregatorImpl>(
						RemoteAggregator.class, 
						new Creator<RemoteAggregatorImpl>() {
							public RemoteAggregatorImpl create() {
								return new RemoteAggregatorImpl(system, null, "districtAggregator");
							}}),
							"districtAggregator");
		System.out.println("# " + gridAggregator.toString() + " deployed");
		try {
			Thread.sleep(Long.MAX_VALUE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static ActorSystem system = ActorSystem.create("grid",ConfigFactory.load().getConfig("grid"));

}
