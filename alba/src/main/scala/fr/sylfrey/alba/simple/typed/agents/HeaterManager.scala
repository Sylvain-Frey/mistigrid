package fr.sylfrey.alba.simple.typed.agents

import akka.actor.TypedActor
import java.util.Date
import fr.sylfrey.alba.simple.resources.HeaterPIDProcessor
import fr.sylfrey.alba.simple.Flexible
import fr.sylfrey.alba.simple.ReduceLoad
import fr.sylfrey.alba.simple.LoadBalancingOrder
import fr.sylfrey.alba.simple.ProsumerStatus
import fr.sylfrey.misTiGriD.temperature.ThermicObject
import fr.sylfrey.alba.simple.SemiFlexible
import fr.sylfrey.misTiGriD.appliances.Heater
import fr.sylfrey.alba.simple.AnyLoad
import fr.sylfrey.alba.simple.Prosumption

trait HeaterManager {
  def getRequiredTemperature : Unit
  def setRequiredTemperature(requiredTemperature : Float) : Unit
  def isEconomizing : Boolean
  def update : Unit
}

class HeaterManagerAgent(
    val heater : Heater, 
    val room : ThermicObject,
    var status : ProsumerStatus,
    var requiredTemperature : Float 
) extends HeaterManager with ProsumerManager {
  
  var heaterProsumption = heater.getEmissionPower()
  var roomTemperature = room.getCurrentTemperature
  var currentOrder : LoadBalancingOrder = AnyLoad
  var isEconomising = false
  
  val pid = new HeaterPIDProcessor(
      maxPower = heater.getMaxEmissionPower(),
      requiredTemperature = this.requiredTemperature,
      currentTemperature = roomTemperature,
      currentPower = heaterProsumption
  )
  
  def getRequiredTemperature = requiredTemperature
  
  def setRequiredTemperature(requiredTemperature : Float) : Unit = {
    this.requiredTemperature = requiredTemperature
  }

  def isEconomizing : Boolean = false
  
  def update = {
      heaterProsumption = heater.getEmissionPower()
      
      (status, currentOrder) match {
        case (Flexible, ReduceLoad) | (SemiFlexible, ReduceLoad) => 
        	pid.requiredTemperature = requiredTemperature - 2
        	isEconomising = true
        case _ => 
        	pid.requiredTemperature = requiredTemperature
        	isEconomising = false
      }
      
	  val newPower = pid.iterate(room.getCurrentTemperature(), heaterProsumption)

	  if (newPower!=heaterProsumption) heater.setEmissionPower(newPower)
  }
  
  def getProsumption = Prosumption(TypedActor.context.self, heaterProsumption, new Date)
  
  def getStatus  = status
  
  def tell(order : LoadBalancingOrder) = this.currentOrder = order

  
}