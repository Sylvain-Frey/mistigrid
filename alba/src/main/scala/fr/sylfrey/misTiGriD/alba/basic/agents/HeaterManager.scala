package fr.sylfrey.misTiGriD.alba.basic.agents

import fr.sylfrey.misTiGriD.temperature.ThermicObject
import fr.sylfrey.misTiGriD.appliances.Heater
import akka.actor.TypedActor
import akka.actor.TypedActor.Receiver
import java.util.Date
import fr.sylfrey.misTiGriD.alba.basic.resources.HeaterPIDProcessor
import fr.sylfrey.misTiGriD.alba.basic.messages.Flexible
import fr.sylfrey.misTiGriD.alba.basic.messages.Ack
import fr.sylfrey.misTiGriD.alba.basic.roles.ProsumerManager
import fr.sylfrey.misTiGriD.alba.basic.messages.ReduceLoad
import fr.sylfrey.misTiGriD.alba.basic.messages.LoadBalancingOrder
import fr.sylfrey.misTiGriD.alba.basic.messages.ProsumerStatus
import fr.sylfrey.misTiGriD.alba.basic.messages.SemiFlexible
import fr.sylfrey.misTiGriD.alba.basic.messages.AnyLoad
import fr.sylfrey.misTiGriD.alba.basic.messages.Prosumption
import fr.sylfrey.misTiGriD.alba.basic.roles.HeaterManager
import fr.sylfrey.misTiGriD.alba.basic.model.Schedule
import fr.sylfrey.misTiGriD.alba.basic.model.Types.P
import fr.sylfrey.misTiGriD.alba.basic.model.EPacket
import fr.sylfrey.misTiGriD.alba.basic.messages.NonFlexible
import fr.sylfrey.misTiGriD.alba.basic.messages.LoadBalancingOrder
import akka.actor.ActorRef
import fr.sylfrey.misTiGriD.alba.basic.messages.LoadBalancingOrder

trait Updatable { def update(): Unit }

trait AlbaHeaterManager extends HeaterManager with ProsumerManager with Updatable with Receiver

class HeaterManagerAgent(
  val heater: Heater,
  val room: ThermicObject,
  var status: ProsumerStatus,
  var requiredTemperature: Float,
  val kp: Float,
  val ki: Float,
  val kd: Float,
  val schedule: Schedule) extends AlbaHeaterManager {

  var heaterProsumption = heater.getEmissionPower()
  var roomTemperature = room.getCurrentTemperature
  var currentOrder: LoadBalancingOrder = AnyLoad
  var _isEconomising = false

  //  val pid = new HeaterPIDProcessor(
  //    maxPower = heater.getMaxEmissionPower(),
  //    requiredTemperature = this.requiredTemperature,
  //    currentTemperature = roomTemperature,
  //    currentPower = heaterProsumption,
  //    kp = kp, 
  //    ki = ki, 
  //    kd = kd)
  def TPlus = requiredTemperature + 2
  def TMinus = requiredTemperature - 1
  var isHeating = false

  def getRequiredTemperature = requiredTemperature

  def setRequiredTemperature(requiredTemperature: Float): Unit = {
    this.requiredTemperature = requiredTemperature
  }

  def isEconomizing: Boolean = _isEconomising

  var packet = EPacket(TypedActor.context.self, 3, 0, status)
  schedule.put(packet, schedule.now)

  def update = {
    heaterProsumption = heater.getEmissionPower()

    val now = schedule.now
    if (room.getCurrentTemperature() < TMinus) { // should heat up

      heater.setEmissionPower(heater.getMaxEmissionPower())
      isHeating = true

    } else if (room.getCurrentTemperature() > TPlus) {

      heater.setEmissionPower(0)
      isHeating = false

    } else { // we're between TMinus and TPlus

      if (isHeating) { // on a heating cycle

        if (room.getCurrentTemperature() < requiredTemperature) { // do continue heating
          heater.setEmissionPower(heater.getMaxEmissionPower())
          isHeating = true
        } else { // interrupt heating in case of load surge
          if (// == ReduceLoad ||
              schedule.getLoadAt(now) == ReduceLoad) {
//            println("# " + heater.getName() + "'s manager shutting down")
            heater.setEmissionPower(0)
            isHeating = false
          } else {
            heater.setEmissionPower(heater.getMaxEmissionPower())
            isHeating = true
          }
        }

      } else { // not on heating cycle

        if (room.getCurrentTemperature() > requiredTemperature) { // do not heat
          isHeating = false
          heater.setEmissionPower(0)
        } else { // possibly anticipate heating
          if (//currentOrder != ReduceLoad &&
            schedule.getLoadAt(now) != ReduceLoad) {
            heater.setEmissionPower(heater.getMaxEmissionPower())
            isHeating = true
          } else {
//            println("# " + heater.getName() + "'s manager not heating")
            heater.setEmissionPower(0)
            isHeating = false
          }
        }

      }

    }

    packetise

  }

  def getProsumption = Prosumption(TypedActor.context.self, heaterProsumption, new Date)

  def getStatus = status

  def setStatus(status: ProsumerStatus) = this.status = status

  def tell(order: LoadBalancingOrder) = {
    this.currentOrder = order
    Ack
  }

  def onReceive(message: Any, sender: ActorRef) = message match {
    case order: LoadBalancingOrder => tell(order)
  }

  private def packetise: Unit = {

    // unrealistic version
    //    val packetStatus = status

    // realistic version
    val packetStatus = room.getCurrentTemperature() < requiredTemperature match {
      case true => NonFlexible
      case false => Flexible
    }
    val newPacket = EPacket(TypedActor.context.self, 3, heater.getEmissionPower(), packetStatus)
    schedule.update(packet, newPacket)
    schedule.move(newPacket, schedule.now)
    packet = newPacket
  }

}