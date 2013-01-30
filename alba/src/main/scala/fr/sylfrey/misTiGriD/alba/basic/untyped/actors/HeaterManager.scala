package fr.sylfrey.misTiGriD.alba.basic.untyped.actors

import fr.sylfrey.misTiGriD.temperature.ThermicObject
import fr.sylfrey.misTiGriD.appliances.Heater
import akka.actor.Actor
import java.util.Date
import fr.sylfrey.misTiGriD.alba.basic.untyped.actors.messages.GetStatus
import fr.sylfrey.misTiGriD.alba.basic.resources.HeaterPIDProcessor
import fr.sylfrey.misTiGriD.alba.basic.messages.Flexible
import fr.sylfrey.misTiGriD.alba.basic.untyped.actors.messages.SetRequiredTemperature
import fr.sylfrey.misTiGriD.alba.basic.untyped.actors.messages.GetProsumption
import fr.sylfrey.misTiGriD.alba.basic.messages.ReduceLoad
import fr.sylfrey.misTiGriD.alba.basic.messages.LoadBalancingOrder
import fr.sylfrey.misTiGriD.alba.basic.messages.ProsumerStatus
import fr.sylfrey.misTiGriD.alba.basic.untyped.actors.messages.SetStatus
import fr.sylfrey.misTiGriD.alba.basic.messages.SemiFlexible
import fr.sylfrey.misTiGriD.alba.basic.messages.AnyLoad
import fr.sylfrey.misTiGriD.alba.basic.untyped.actors.messages.Tick
import fr.sylfrey.misTiGriD.alba.basic.messages.Prosumption


class HeaterManager(
    val heater : Heater, 
    val room : ThermicObject,
    var status : ProsumerStatus,
    var requiredTemperature : Float 
) extends Actor {
    
  val pid = new HeaterPIDProcessor(
      maxPower = heater.getMaxEmissionPower(),
      requiredTemperature = this.requiredTemperature,
      currentTemperature = room.getCurrentTemperature(),
      currentPower = heaterProsumption
  )
  var heaterProsumption = 0f
  var currentOrder : LoadBalancingOrder = AnyLoad
  var isEconomising = false
  
  def receive = {
    
    case SetRequiredTemperature(temperature) => this.requiredTemperature = temperature
    
  	case GetProsumption => sender ! Prosumption(self, heaterProsumption, new Date)
    
    case GetStatus => sender ! status
    case SetStatus(status) => this.status = status
    
    case order : LoadBalancingOrder => this.currentOrder = order
    
    case Tick => 
      heaterProsumption = heater.getEmissionPower()
      (status, currentOrder) match {
        case (Flexible, ReduceLoad) | (SemiFlexible, ReduceLoad) => 
        	pid.requiredTemperature = requiredTemperature - 2
        	isEconomising = true
        case _ => 
        	pid.requiredTemperature = requiredTemperature
        	isEconomising = false
      }
      
	  val newPower = pid.iterate(room.getCurrentTemperature(), heaterProsumption);

	  if (newPower!=heaterProsumption) heater.setEmissionPower(newPower)
		
		
    case unknown => System.err.println("# " + self + " received unknown message " + unknown); 
    
  }
  
}