/*******************************************************************************
 * Copyright (c) 2013 EDF. This software was developed with the 
 * collaboration of Télécom ParisTech (Sylvain Frey).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Sylvain Frey - initial API and implementation
 ******************************************************************************/
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
import scala.collection.mutable.Queue
import scala.util.Sorting

trait HouseLoadManager extends LoadManager with ProsumerManager with Updatable with Receiver

class HouseLoadManagerAgent(
  val aggregator: Aggregator,
  var maxConsumption: Float,
  val hysteresisThreshold: Float,
  val loadReductionDelta : Float,
  val acceptableLoadRange: Float,
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

  def setMaximumProsumption(threshold: Float) = currentOrder match {
    case ReduceLoad => {
      baseMaxConsumption = threshold - loadReductionDelta
    }
    case AnyLoad => {
      baseMaxConsumption = threshold
    }
    case RiseLoad => {
      baseMaxConsumption = threshold + loadReductionDelta
    }
  }

  def getProsumption = Prosumption(TypedActor.context.self, currentAggregatedProsumption, new Date)

  def maxConsumptionThreshold = maxConsumption

  def minConsumptionThreshold = minConsumption

  def getStatus = status

  def setStatus(status: ProsumerStatus) = this.status = status

  def update = {
    currentAggregatedProsumption = aggregator.getProsumedPower()

    currentOrder match {
      case ReduceLoad => {
        maxConsumption = baseMaxConsumption + loadReductionDelta
        status = NonFlexible
      }
      case AnyLoad => {
        maxConsumption = baseMaxConsumption
        status = Flexible
      }
      case RiseLoad => {
        maxConsumption = baseMaxConsumption - loadReductionDelta
        status = NonFlexible
      }
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
//            println("# load manager feels high load but no one can help him")
          case Some(packet) =>
          	packet.device ! ReduceLoad
          	erasedProsumers += packet.device
//          	println("# load manager told " + ReduceLoad + " to " + packet.device)
        }
        
        
      } else if (currentAggregatedProsumption > minConsumption) {
        
        Random.shuffle(erasedProsumers).headOption match {
          case None =>
          case Some(device) =>
            device ! AnyLoad
            erasedProsumers -= device
//          	println("# load manager told " + AnyLoad + " to " + device)
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
    
    /////
    // let's stat
    /*history.enqueue((currentAggregatedProsumption, maxConsumption))
    if (history.size>historyLength) history.dequeue
    
    val oldMu = mu
    
    history.foldLeft((0:P, 0:P, 0:P, new Array[P](maxesSize))) { 
      case ((mu, muError, sigma, maxes), (prosumption, objective)) =>
      	val error = objective - prosumption
      	val delta = prosumption - oldMu
      	if (prosumption < maxes(maxesSize-1)) maxes(maxesSize-1) = prosumption
      	Sorting.quickSort(maxes)
      	// (mean consumption, mean overshoot, standard deviation, max 10 values)
      	(mu + prosumption, muError + Math.max(error,0), sigma + delta*delta, maxes)  
    } match { case (newMu, newMuError, newSigma, maxes) =>
      mu = newMu / historyLength
      muError = newMuError / historyLength
      sigma = (Math.sqrt(newSigma) / historyLength).toFloat
      	println("# " + maxes.foldLeft("") { (string, p) => string + " " + p})
      maxDecile = maxes(maxesSize-1)
      muMax = maxes.reduce { (sum, p) => sum + p } / maxesSize
    }
    
    println("# mu=" + mu + " muError=" + muError + " sigma=" + sigma + " maxDecile=" + maxDecile + " muMax=" + muMax)
*/
    
  }
  
    val historyLength = 20*10 // 20*X seconds history with period 50ms
    val history = new Queue[Tuple2[P,P]]()
    val maxesSize = 10
    var (mu, muError, sigma, maxDecile, muMax) = (0:P, 0:P, 0:P,0:P, 0:P)

  def tell(order: LoadBalancingOrder) = {
    this.currentOrder = order
    println("# " + aggregator.getName() + "'s manager has been told " + order)
    Ack
  }
  def onReceive(message: Any, sender: ActorRef) = message match {
    case order: LoadBalancingOrder => tell(order)
  }

  var baseMaxConsumption = maxConsumption
  def minConsumption = maxConsumption + acceptableLoadRange
  
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
