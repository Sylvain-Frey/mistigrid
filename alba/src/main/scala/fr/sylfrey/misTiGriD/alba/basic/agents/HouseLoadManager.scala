package fr.sylfrey.misTiGriD.alba.basic.agents

import java.util.Date

import scala.Array.canBuildFrom
import scala.collection.mutable.LinkedHashMap
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Random

import akka.actor.ActorRef
import akka.actor.TypedActor
import akka.actor.TypedActor.Receiver
import akka.actor.TypedProps
import akka.actor.actorRef2Scala
import fr.sylfrey.misTiGriD.alba.basic.messages.Ack
import fr.sylfrey.misTiGriD.alba.basic.messages.AnyLoad
import fr.sylfrey.misTiGriD.alba.basic.messages.Flexible
import fr.sylfrey.misTiGriD.alba.basic.messages.LoadBalancingOrder
import fr.sylfrey.misTiGriD.alba.basic.messages.NonFlexible
import fr.sylfrey.misTiGriD.alba.basic.messages.ProsumerStatus
import fr.sylfrey.misTiGriD.alba.basic.messages.Prosumption
import fr.sylfrey.misTiGriD.alba.basic.messages.ReduceLoad
import fr.sylfrey.misTiGriD.alba.basic.messages.RiseLoad
import fr.sylfrey.misTiGriD.alba.basic.model.EPacket
import fr.sylfrey.misTiGriD.alba.basic.model.Schedule
import fr.sylfrey.misTiGriD.alba.basic.model.Types.P
import fr.sylfrey.misTiGriD.alba.basic.roles.LoadManager
import fr.sylfrey.misTiGriD.alba.basic.roles.ProsumerManager
import fr.sylfrey.misTiGriD.electricalGrid.Aggregator

trait HouseLoadManager extends LoadManager with ProsumerManager with Updatable with Receiver

class HouseLoadManagerAgent(
  val aggregator: Aggregator,
  var maxConsumption: Float,
  val hysteresisThreshold: Float,
  var status: ProsumerStatus,
  val localSchedule: Schedule,
  val fatherSchedule: Option[Schedule]) extends HouseLoadManager {

  implicit val executionContext: ExecutionContext = TypedActor.context.system.dispatcher

  val prosumers = new LinkedHashMap[ActorRef, ProsumerManager]()
  val reduceLoadPrs = new LinkedHashMap[ActorRef, ProsumerManager]()
  val riseLoadPrs = new LinkedHashMap[ActorRef, ProsumerManager]()
  
  val erasedProsumers = new ListBuffer[ActorRef]()

  var currentOrder: LoadBalancingOrder = AnyLoad
  var currentAggregatedProsumption: Float = 0f

  val sliceSize = 40
  val packets = maxPacketise(sliceSize)
  fatherSchedule match {
    case Some(schedule) =>
      packets.indices foreach { index =>
        schedule.put(packets(index)._1, index * sliceSize)
        schedule.put(packets(index)._2, index * sliceSize)
      }
    case None =>
  }

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

  def minConsumptionThreshold = minConsumption

  def getStatus = status

  def setStatus(status: ProsumerStatus) = this.status = status

  def update = {
    currentAggregatedProsumption = aggregator.getProsumedPower()

    currentOrder match {
      case ReduceLoad => maxConsumption = baseMaxConsumption + loadReductionDelta
      case AnyLoad => maxConsumption = baseMaxConsumption
      case RiseLoad => maxConsumption = baseMaxConsumption - loadReductionDelta
    }

      val now = localSchedule.now
    // set schedule goal, beware of reverse prosumption convention...
    localSchedule.setGoal(0, localSchedule.size - 1, -maxConsumption)
    if (localSchedule.goal(now) < localSchedule.total(now)
          || currentOrder == ReduceLoad
          ||currentAggregatedProsumption < maxConsumption) localSchedule.setLoad(ReduceLoad, now)
    else localSchedule.setLoad(AnyLoad, now)

//    localSchedule.schedule.indices foreach { t =>

    
      if (localSchedule.goal(now) < localSchedule.total(now)
          || currentOrder == ReduceLoad
          || currentAggregatedProsumption < maxConsumption) { // high load time
        
        // find a prosumer and erase it, if any
        val candidates = localSchedule.get(now).filter { packet => 
          packet.status == Flexible &&
          !erasedProsumers.contains(packet.device)
        }
        Random.shuffle(candidates).headOption match {
          case None =>
            println("# load manager feels high load but no one can help him")
          case Some(packet) =>
          	packet.device ! ReduceLoad
          	erasedProsumers += packet.device
          	println("# load manager told " + ReduceLoad + " to " + packet.device)
        }
        
        
      } else if (currentAggregatedProsumption > minConsumption) {
        
        Random.shuffle(erasedProsumers).headOption match {
          case None =>
          case Some(device) =>
            device ! AnyLoad
            erasedProsumers -= device
          	println("# load manager told " + AnyLoad + " to " + device)
        }
      }

//    }

    // update father schedule
    fatherSchedule match {
      case Some(schedule) =>
        val newPackets = maxPacketise(sliceSize)

        (packets zip newPackets) foreach { case (oldPacket, newPacket) =>
          schedule.update(oldPacket._1, newPacket._1)
          schedule.update(oldPacket._2, newPacket._2)
        }

        newPackets copyToArray packets
      case None =>
    }

    //    /*
    //     * There are 5 possible prosumption domains: 
    //     * 		p < maxConsumption
    //     * 		maxConsumption <= p <= maxConsumption + hysteresisThreshold
    //     * 		maxConsumption + hysteresisThreshold < p < minConsumption - hysteresisThreshold
    //     * 		minConsumption - hysteresisThreshold <= p <= minConsumption
    //     * 		minConsumption < p
    //     */
    //    
    //    if (currentAggregatedProsumption < maxConsumption) { // load too high, reduce it
    //
    //      if (!prosumers.isEmpty) {
    //        tell_ToOne_AndMoveItTo_(ReduceLoad, prosumers, reduceLoadPrs)
    //      } else { // can't do anything more
    //        println("# " + aggregator.getName() + "'s manager has no available prosumers left for reducing load")
    //      }
    //
    //    } 
    //    
    //    if (currentAggregatedProsumption > maxConsumption + hysteresisThreshold) { // load not so high
    //
    //      if (!reduceLoadPrs.isEmpty) {
    //        tell_ToOne_AndMoveItTo_(AnyLoad, reduceLoadPrs, prosumers)
    //      } 
    //      
    //    }
    //    
    //    if (currentAggregatedProsumption < minConsumption - hysteresisThreshold) { // load not so low
    //
    //      if (!riseLoadPrs.isEmpty) {
    //        tell_ToOne_AndMoveItTo_(AnyLoad, riseLoadPrs, prosumers)
    //      } 
    //      
    //    } 
    //    
    //    if (currentAggregatedProsumption > minConsumption) { // load too low, rise it
    //      
    //      if (!prosumers.isEmpty) {
    //        tell_ToOne_AndMoveItTo_(RiseLoad, prosumers, riseLoadPrs)
    //      } else { // can't do anything more
    //        println("# " + aggregator.getName() + "'s manager has no available prosumers left for rising load")
    //      }
    //      
    //    }

  }

  def tell(order: LoadBalancingOrder) = {
    this.currentOrder = order
    println("# " + aggregator.getName() + "'s manager has been told " + order)
    Ack
  }
  def onReceive(message: Any, sender: ActorRef) = message match {
    case order: LoadBalancingOrder => tell(order)
  }

  var baseMaxConsumption = maxConsumption
  val loadReductionDelta = 500
  val acceptableLoadRange = 1000
  def minConsumption = maxConsumption + acceptableLoadRange
  
  private def tell_ToOne_AndMoveItTo_(
    order: LoadBalancingOrder,
    src: LinkedHashMap[ActorRef, ProsumerManager],
    dest: LinkedHashMap[ActorRef, ProsumerManager]) = {

    val (ref, targetProsumer) = src.head
    Future { targetProsumer.tell(order) }
    println("# " + aggregator.getName() + "'s manager told " + order + " to " + targetProsumer)
    src.remove(ref)
    dest.put(ref, targetProsumer)

  }

  private def maxPacketise(sliceSize: Int) = {

    val hull = localSchedule.schedule.indices.map { t =>
      val flexTotal = localSchedule.get(t).filter { packet =>
        packet.status == Flexible
      }.foldLeft(0:P) { (sum, packet) =>
        sum + packet.max
      }
      val nonFlexTotal = localSchedule.get(t).filter { packet =>
        packet.status == NonFlexible
      }.foldLeft(0:P) { (sum, packet) =>
        sum + packet.max
      }
      (flexTotal, nonFlexTotal)
    }

    
    val maxes = hull.grouped(sliceSize).map { slice =>
      slice.foldLeft((Float.MinValue, Float.MinValue)) { (max, localMax) =>
        (Math.max(max._1, localMax._1), Math.max(max._2, localMax._2))
      }
    }

    val newPackets = maxes.map { max =>
      (EPacket(TypedActor.context.self, sliceSize, max._1, Flexible),
      EPacket(TypedActor.context.self, sliceSize, max._2, NonFlexible))
    }

    newPackets.toArray

  }

} 