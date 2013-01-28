package fr.sylfrey.alba.simple.actors.agents

import java.util.Date
import akka.actor.Actor
import fr.sylfrey.alba.simple.actors.messages.GetStatus
import fr.sylfrey.alba.simple.resources.HeaterPIDProcessor
import fr.sylfrey.alba.simple.Flexible
import fr.sylfrey.alba.simple.actors.messages.SetRequiredTemperature
import fr.sylfrey.alba.simple.actors.messages.GetProsumption
import fr.sylfrey.alba.simple.ReduceLoad
import fr.sylfrey.alba.simple.LoadBalancingOrder
import fr.sylfrey.alba.simple.ProsumerStatus
import fr.sylfrey.alba.simple.actors.messages.SetStatus
import fr.sylfrey.misTiGriD.temperature.ThermicObject
import fr.sylfrey.alba.simple.SemiFlexible
import fr.sylfrey.misTiGriD.appliances.Heater
import fr.sylfrey.alba.simple.AnyLoad
import fr.sylfrey.alba.simple.actors.messages.Tick
import fr.sylfrey.alba.simple.Prosumption
import fr.sylfrey.alba.simple.actors.messages.SetRequiredTemperature


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