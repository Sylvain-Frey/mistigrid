package fr.sylfrey.misTiGriD.alba.basic.agents

import fr.sylfrey.misTiGriD.temperature.ThermicObject
import fr.sylfrey.misTiGriD.appliances.Heater
import akka.actor.TypedActor
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

trait Updatable { def update(): Unit }

trait AlbaHeaterManager extends HeaterManager with ProsumerManager with Updatable

class HeaterManagerAgent(
  val heater: Heater,
  val room: ThermicObject,
  var status: ProsumerStatus,
  var requiredTemperature: Float,
  var kp : Float,
  var ki : Float,
  var kd : Float	
) extends AlbaHeaterManager {

  var heaterProsumption = heater.getEmissionPower()
  var roomTemperature = room.getCurrentTemperature
  var currentOrder: LoadBalancingOrder = AnyLoad
  var _isEconomising = false

  val pid = new HeaterPIDProcessor(
    maxPower = heater.getMaxEmissionPower(),
    requiredTemperature = this.requiredTemperature,
    currentTemperature = roomTemperature,
    currentPower = heaterProsumption,
    kp = kp, 
    ki = ki, 
    kd = kd)

  def getRequiredTemperature = requiredTemperature

  def setRequiredTemperature(requiredTemperature: Float): Unit = {
    this.requiredTemperature = requiredTemperature
  }

  def isEconomizing: Boolean = _isEconomising

  def update = {
    heaterProsumption = heater.getEmissionPower()

    (status, currentOrder) match {
      case (Flexible, ReduceLoad) | (SemiFlexible, ReduceLoad) =>
        pid.requiredTemperature = requiredTemperature - 2
        _isEconomising = true
      case _ =>
        pid.requiredTemperature = requiredTemperature
        _isEconomising = false
    }

    val newPower = pid.iterate(room.getCurrentTemperature(), heaterProsumption)

    if (newPower != heaterProsumption) heater.setEmissionPower(newPower)
  }

  def getProsumption = Prosumption(TypedActor.context.self, heaterProsumption, new Date)

  def getStatus = status
  
  def setStatus(status: ProsumerStatus) = this.status = status

  def tell(order: LoadBalancingOrder) = {
    this.currentOrder = order
    Ack
  }

}