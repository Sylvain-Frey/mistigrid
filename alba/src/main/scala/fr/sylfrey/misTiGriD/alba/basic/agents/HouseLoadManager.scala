package fr.sylfrey.misTiGriD.alba.basic.agents

import java.util.Date

import scala.collection.mutable.LinkedHashMap
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import akka.actor.ActorRef
import akka.actor.TypedActor
import akka.actor.TypedProps
import fr.sylfrey.misTiGriD.alba.basic.messages.Ack
import fr.sylfrey.misTiGriD.alba.basic.messages.AnyLoad
import fr.sylfrey.misTiGriD.alba.basic.messages.LoadBalancingOrder
import fr.sylfrey.misTiGriD.alba.basic.messages.ProsumerStatus
import fr.sylfrey.misTiGriD.alba.basic.messages.Prosumption
import fr.sylfrey.misTiGriD.alba.basic.messages.ReduceLoad
import fr.sylfrey.misTiGriD.alba.basic.roles.LoadManager
import fr.sylfrey.misTiGriD.alba.basic.roles.ProsumerManager
import fr.sylfrey.misTiGriD.electricalGrid.Aggregator
  
trait HouseLoadManager extends LoadManager with ProsumerManager with Updatable

class HouseLoadManagerAgent(
    val aggregator : Aggregator,
    var maxConsumption : Float,
    val hysteresisThreshold : Float,
    var status : ProsumerStatus
) extends HouseLoadManager {
  
  implicit val executionContext : ExecutionContext = TypedActor.context.system.dispatcher
  
  val prosumers = new LinkedHashMap[ActorRef,ProsumerManager]()
  val erasedProsumers = new LinkedHashMap[ActorRef,ProsumerManager]()
  
  var currentOrder : LoadBalancingOrder = AnyLoad
  var currentAggregatedProsumption : Float = 0f
  
  def register(prosumer : ActorRef) = {
    val proxy = TypedActor(TypedActor.context.system).typedActorOf(
        TypedProps[ProsumerManager],
        prosumer)
    prosumers.put(prosumer,proxy)
    println("# loadManager registered " + prosumer)
  }
  
  def unregister(prosumer : ActorRef) = {
    prosumers.remove(prosumer)
    erasedProsumers.remove(prosumer)
  }  
  
  def setMaximumProsumption(threshold : Float) = this.maxConsumption = threshold
  
  def getProsumption = Prosumption(TypedActor.context.self, currentAggregatedProsumption, new Date)
  
  def maxConsumptionThreshold = maxConsumption
  
  def getStatus = status
  
  def setStatus(status : ProsumerStatus) = this.status = status

  def update = {
    currentAggregatedProsumption = aggregator.getProsumedPower()
    
    if (currentOrder == ReduceLoad) maxConsumption = baseMaxConsumption + 500
    else maxConsumption = baseMaxConsumption
      
    if (currentAggregatedProsumption < maxConsumption && !prosumers.isEmpty) {
			
	  val (ref, erasedProsumer) = prosumers.head
	  Future { erasedProsumer.tell(ReduceLoad) }
	  println("# " + aggregator.getName() + "'s manager told " + ReduceLoad + " to " + erasedProsumer)
	  prosumers.remove(ref)
	  erasedProsumers.put(ref, erasedProsumer)
			
	} else if (currentAggregatedProsumption > maxConsumption + hysteresisThreshold && !erasedProsumers.isEmpty) {
			
	  val (ref, unerasedProsumer) = erasedProsumers.head
	  Future { unerasedProsumer.tell(AnyLoad) }
	  println("# " + aggregator.getName() + "'s manager told " + AnyLoad + " to " + unerasedProsumer)
	  erasedProsumers.remove(ref)
	  prosumers.put(ref, unerasedProsumer)
			
	}
  }
  
  def tell(order : LoadBalancingOrder) = {
    this.currentOrder = order
	println("# " + aggregator.getName() + "'s manager has been told " + order)
    Ack
  }
  
  val baseMaxConsumption = maxConsumption
  
} 