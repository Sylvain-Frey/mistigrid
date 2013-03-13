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
import fr.sylfrey.misTiGriD.alba.basic.messages.RiseLoad

trait HouseLoadManager extends LoadManager with ProsumerManager with Updatable

class HouseLoadManagerAgent(
  val aggregator: Aggregator,
  var maxConsumption: Float,
  val hysteresisThreshold: Float,
  var status: ProsumerStatus) extends HouseLoadManager {

  implicit val executionContext: ExecutionContext = TypedActor.context.system.dispatcher

  val prosumers = new LinkedHashMap[ActorRef, ProsumerManager]()
  val reduceLoadPrs = new LinkedHashMap[ActorRef, ProsumerManager]()
  val riseLoadPrs = new LinkedHashMap[ActorRef, ProsumerManager]()

  var currentOrder: LoadBalancingOrder = AnyLoad
  var currentAggregatedProsumption: Float = 0f

  def register(prosumer: ActorRef) = {
    val proxy = TypedActor(TypedActor.context.system).typedActorOf(
      TypedProps[ProsumerManager],
      prosumer)
    prosumers.put(prosumer, proxy)
    println("# loadManager registered " + prosumer)
  }

  def unregister(prosumer: ActorRef) = {
    prosumers.remove(prosumer)
    reduceLoadPrs.remove(prosumer)
    riseLoadPrs.remove(prosumer)
  }

  def setMaximumProsumption(threshold: Float) = baseMaxConsumption = threshold

  def getProsumption = Prosumption(TypedActor.context.self, currentAggregatedProsumption, new Date)

  def maxConsumptionThreshold = maxConsumption

  def getStatus = status

  def setStatus(status: ProsumerStatus) = this.status = status

  def update = {
    currentAggregatedProsumption = aggregator.getProsumedPower()

    currentOrder match {
      case ReduceLoad => maxConsumption = baseMaxConsumption + loadReductionDelta
      case AnyLoad => maxConsumption = baseMaxConsumption
      case RiseLoad => maxConsumption = baseMaxConsumption - loadReductionDelta
    }

    /*
     * There are 5 possible prosumption domains: 
     * 		p < maxConsumption
     * 		maxConsumption <= p <= maxConsumption + hysteresisThreshold
     * 		maxConsumption + hysteresisThreshold < p < minConsumption - hysteresisThreshold
     * 		minConsumption - hysteresisThreshold <= p <= minConsumption
     * 		minConsumption < p
     */
    
    if (currentAggregatedProsumption < maxConsumption) { // load too high, reduce it

      if (!prosumers.isEmpty) {
        tell_ToOne_AndMoveItTo_(ReduceLoad, prosumers, reduceLoadPrs)
      } else { // can't do anything more
        println("# " + aggregator.getName() + "'s manager has no available prosumers left for reducing load")
      }

    } 
    
    if (currentAggregatedProsumption > maxConsumption + hysteresisThreshold) { // load not so high

      if (!reduceLoadPrs.isEmpty) {
        tell_ToOne_AndMoveItTo_(AnyLoad, reduceLoadPrs, prosumers)
      } 
      
    }
    
    if (currentAggregatedProsumption < minConsumption - hysteresisThreshold) { // load not so low

      if (!riseLoadPrs.isEmpty) {
        tell_ToOne_AndMoveItTo_(AnyLoad, riseLoadPrs, prosumers)
      } 
      
    } 
    
    if (currentAggregatedProsumption > minConsumption) { // load too low, rise it
      
      if (!prosumers.isEmpty) {
        tell_ToOne_AndMoveItTo_(RiseLoad, prosumers, riseLoadPrs)
      } else { // can't do anything more
        println("# " + aggregator.getName() + "'s manager has no available prosumers left for rising load")
      }
      
    }
    
  }

  def tell(order: LoadBalancingOrder) = {
    this.currentOrder = order
    println("# " + aggregator.getName() + "'s manager has been told " + order)
    Ack
  }

  var baseMaxConsumption = maxConsumption
  val loadReductionDelta = 500
  val acceptableLoadRange = 1000
  def minConsumption = maxConsumption + acceptableLoadRange
  
  private def tell_ToOne_AndMoveItTo_(
      order : LoadBalancingOrder, 
      src : LinkedHashMap[ActorRef, ProsumerManager], 
      dest : LinkedHashMap[ActorRef, ProsumerManager]) = {
    
    val (ref, targetProsumer) = src.head
    Future { targetProsumer.tell(order) }
    println("# " + aggregator.getName() + "'s manager told " + order + " to " + targetProsumer)
    src.remove(ref)
    dest.put(ref, targetProsumer)
    
  }

} 