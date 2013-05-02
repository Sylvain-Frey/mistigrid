package fr.sylfrey.misTiGriD.alba.basic.model

import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer
import Types._
import akka.actor.ActorRef
import org.apache.felix.ipojo.annotations.Instantiate
import org.apache.felix.ipojo.annotations.Provides
import org.apache.felix.ipojo.annotations.Component
import org.apache.felix.ipojo.annotations.Requires
import fr.sylfrey.misTiGriD.environment.Time
import org.apache.felix.ipojo.annotations.Validate
import fr.sylfrey.misTiGriD.alba.basic.messages.ProsumerStatus
import org.osgi.framework.BundleContext
import fr.sylfrey.akka.ActorSystemProvider
import akka.actor.TypedActor
import akka.actor.TypedProps
import fr.sylfrey.misTiGriD.alba.basic.messages.LoadBalancingOrder
import fr.sylfrey.misTiGriD.alba.basic.messages.AnyLoad

trait Schedule {

  def size: Int
  def now: T

  def schedule: Array[ListBuffer[EPacket]]
  def packets: HashMap[EPacket, T]
  def goal: Array[P]
  def load: Array[LoadBalancingOrder]

  def put(packet: EPacket, start: T): Unit = {
    
    testRangeValidity(start)

    packets += ((packet, start))
    for (t <- start to start + packet.length - 1) schedule(t % size) += packet

  }

  def get(t: T): ListBuffer[EPacket] = {
    testRangeValidity(t)
    schedule(t)
  }

  def move(packet: EPacket, newStart: T): Unit = {
    testRangeValidity(newStart)
    remove(packet)
    put(packet, newStart)
  }

  def update(oldPacket: EPacket, newPacket: EPacket): Unit = {
    put(newPacket, packets(oldPacket))
    remove(oldPacket)
  }

  def remove(packet: EPacket): Unit = {
    val start = packets(packet)
    val end = start + packet.length - 1
    for (t <- start to end) {
      if (!schedule(t % size).isEmpty && schedule(t % size).contains(packet)) {
        schedule(t % size) -= packet
      }
    }
    packets -= packet
  }

  def setGoal(start: T, end: T, level: P) = {
    (start to end) foreach { goal(_) = level }
  }
  
  def setLoad(order: LoadBalancingOrder, t: T) = {
    load(t) = order
  }

  def getLoadAt(t: T) = {
    load(t)
  }
  
  def total(t: T): P = {
    schedule(t).foldLeft(0: P) { (sum, packet) =>
      sum + packet.max
    }
  }

  def tick(): Unit = {
    packets.foreach {
      case (packet, t) =>
        if (t != 0) move(packet, t - 1)
        else move(packet, schedule.size - 1)
    }
    goal.indices.foreach { index => goal(index) = goal((index + 1) % goal.size) }
  }

  private def testRangeValidity(t: T) = if (t < 0 || t >= size) {
    throw new IllegalArgumentException("# out of range schedule index " + t)
  }

  def checkScheduleConsistency(): Unit = {

    val expected = packets.foldLeft(0: P) {
      case (sum, (packet, t)) => sum + packet.length * packet.max
    }

    val energy = schedule.foldLeft(0: P) { (sum, list) =>
      sum + list.foldLeft(0: P) { (sum, packet) =>
        sum + packet.max
      }
    }

    assert(energy == expected, {
      println("# energy = " + energy + " != " + expected)
      println("# packets :")
      packets.foreach {
        case (packet, t) =>
          println(packet.max + " * " + packet.length + " @ " + t)
      }
      println("# schedule :")
      schedule.indices.foreach { index =>
        val list = schedule(index).foldLeft("") { (string, packet) =>
          string + " + " + packet.max
        }
        println(index + " : " + list)
      }
    })
    //println("# integrity checked: " + energy + " = " + expected)
  }

}

class ScheduleImpl(
  val time: Time,
  val size: Int) extends Schedule {

  def now = time.dayTime().asInstanceOf[Int] / 1000

  val schedule = new Array[EPacket](size).map { _ => new ListBuffer[EPacket]() }
  val packets = new HashMap[EPacket, T]()
  val goal = new Array[P](size) map { _ => 0: P }
  val load = new Array[LoadBalancingOrder](size) map { _ => AnyLoad: LoadBalancingOrder}

}
