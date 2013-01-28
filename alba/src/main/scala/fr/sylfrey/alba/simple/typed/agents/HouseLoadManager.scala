package fr.sylfrey.alba.simple.typed.agents

import java.util.Random
import akka.actor.ActorRef
import akka.actor.TypedActor
import akka.actor.TypedProps
import scala.collection.mutable.LinkedHashMap
import java.util.Date
import fr.sylfrey.alba.simple.ReduceLoad
import fr.sylfrey.alba.simple.LoadBalancingOrder
import fr.sylfrey.alba.simple.ProsumerStatus
import fr.sylfrey.misTiGriD.electricalGrid.Aggregator
import fr.sylfrey.alba.simple.AnyLoad
import fr.sylfrey.alba.simple.Prosumption

trait HouseLoadManager extends LoadManager with ProsumerManager {
  def setMaximumProsumption(threshold : Float) : Unit
  def setStatus(status : ProsumerStatus) : Unit
  def update : Unit
}

class HouseLoadManagerAgent(
    val aggregator : Aggregator,
    var maxConsumption : Float,
    val hysteresisThreshold : Float,
    var status : ProsumerStatus
) extends HouseLoadManager {
  
  val prosumers = new LinkedHashMap[ActorRef,ProsumerManager]()
  val erasedProsumers = new LinkedHashMap[ActorRef,ProsumerManager]()
  
  var currentOrder : LoadBalancingOrder = AnyLoad
  var currentAggregatedProsumption : Float = 0f
  
  def register(prosumer : ActorRef) = {
    val proxy = TypedActor(TypedActor.context.system).typedActorOf(
        TypedProps[ProsumerManager],
        prosumer)
    prosumers.put(prosumer,proxy)
  }
  
  def unregister(prosumer : ActorRef) = {
    prosumers.remove(prosumer)
    erasedProsumers.remove(prosumer)
  }  
  
  def setMaximumProsumption(threshold : Float) = this.maxConsumption = threshold
  
  def getProsumption = Prosumption(TypedActor.context.self, currentAggregatedProsumption, new Date)
  
  def getStatus = status
  
  def setStatus(status : ProsumerStatus) = this.status = status

  def update = {
    currentAggregatedProsumption = aggregator.getProsumedPower()
      
    if (currentAggregatedProsumption < maxConsumption && !prosumers.isEmpty) {
			
	  val (ref, erasedProsumer) = prosumers.head
	  erasedProsumer.tell(ReduceLoad)
	  prosumers.remove(ref)
	  erasedProsumers.put(ref, erasedProsumer)
			
	} else if (currentAggregatedProsumption > maxConsumption + hysteresisThreshold && !erasedProsumers.isEmpty) {
			
	  val (ref, unerasedProsumer) = erasedProsumers.head
	  unerasedProsumer.tell(AnyLoad)
	  erasedProsumers.remove(ref)
	  prosumers.put(ref, unerasedProsumer)
			
	}
  }
  
  def tell(order : LoadBalancingOrder) = this.currentOrder = order
  
} 