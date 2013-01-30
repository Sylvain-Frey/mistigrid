package fr.sylfrey.misTiGriD.alba.basic.untyped.actors

import fr.sylfrey.misTiGriD.electricalGrid.Aggregator
import java.util.Random
import java.util.LinkedList
import akka.actor.Actor
import akka.actor.ActorRef
import fr.sylfrey.misTiGriD.alba.basic.untyped.actors.messages.GetStatus
import fr.sylfrey.misTiGriD.alba.basic.untyped.actors.messages.SetMaximumProsumption
import fr.sylfrey.misTiGriD.alba.basic.messages.ReduceLoad
import fr.sylfrey.misTiGriD.alba.basic.messages.LoadBalancingOrder
import fr.sylfrey.misTiGriD.alba.basic.messages.ProsumerStatus
import fr.sylfrey.misTiGriD.alba.basic.untyped.actors.messages.Unregister
import fr.sylfrey.misTiGriD.alba.basic.untyped.actors.messages.SetStatus
import fr.sylfrey.misTiGriD.alba.basic.untyped.actors.messages.Register
import fr.sylfrey.misTiGriD.alba.basic.messages.AnyLoad
import fr.sylfrey.misTiGriD.alba.basic.untyped.actors.messages.Tick

class HouseLoadManager(
    val aggregator : Aggregator,
    var maxConsumption : Float,
    val hysteresisThreshold : Float,
    var status : ProsumerStatus
) extends Actor {
  
  val prosumers = new LinkedList[ActorRef]()
  val erasedProsumers = new LinkedList[ActorRef]()
  val random = new Random
  
  var currentOrder : LoadBalancingOrder = AnyLoad
  var currentAggregatedProsumption : Float = 0f
   
  
  def receive = {
    
    case SetMaximumProsumption(threshold) => this.maxConsumption = threshold
    
    case order : LoadBalancingOrder => this.currentOrder = order
    
    case GetStatus => sender ! status
    case SetStatus(status) => this.status = status
    
    case Register(prosumer) => prosumers.add(prosumer)
    
    case Unregister(prosumer) => 
      prosumers.remove(prosumer)
      erasedProsumers.remove(prosumer)
      
    case Tick => 
      currentAggregatedProsumption = aggregator.getProsumedPower()
      
      if (currentAggregatedProsumption < maxConsumption && !prosumers.isEmpty()) {
			
		val erasedProsumer = prosumers.remove(random.nextInt(prosumers.size()))
		erasedProsumer ! ReduceLoad
		erasedProsumers.add(erasedProsumer)
			
	  } else if (currentAggregatedProsumption > maxConsumption + hysteresisThreshold && !erasedProsumers.isEmpty()) {
			
		val unerasedProsumer = erasedProsumers.remove(random.nextInt(erasedProsumers.size()));
		unerasedProsumer ! AnyLoad
		prosumers.add(unerasedProsumer);
			
	  }
      
    case unknown => System.err.println("# " + self + " received unknown message " + unknown);
            
  }

}