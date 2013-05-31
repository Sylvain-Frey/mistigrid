package fr.sylfrey.misTiGriD.alba.basic.agents

import fr.sylfrey.misTiGriD.alba.basic.roles.ProsumerManager
import fr.sylfrey.misTiGriD.electricalGrid.Lamp
import fr.sylfrey.misTiGriD.alba.basic.messages.ProsumerStatus
import fr.sylfrey.misTiGriD.alba.basic.messages.Prosumption
import akka.actor.TypedActor
import java.util.Date
import fr.sylfrey.misTiGriD.alba.basic.messages.LoadBalancingOrder
import fr.sylfrey.misTiGriD.alba.basic.messages.LoadBalancingOrderResponse
import fr.sylfrey.misTiGriD.alba.basic.messages.AnyLoad
import fr.sylfrey.misTiGriD.alba.basic.messages.Flexible
import fr.sylfrey.misTiGriD.alba.basic.messages.ReduceLoad
import fr.sylfrey.misTiGriD.alba.basic.messages.Ack
import fr.sylfrey.misTiGriD.alba.basic.model.Schedule
import fr.sylfrey.misTiGriD.alba.basic.model.EPacket

trait LampManager extends ProsumerManager with Updatable {
  def isEconomising : Boolean
}

class LampManagerAgent(
  val lamp: Lamp,
  var status: ProsumerStatus,
  val ecoMaxPower: Float,
  val schedule: Schedule
) extends LampManager {
  
  var currentOrder: LoadBalancingOrder = AnyLoad
  
  def getProsumption(): Prosumption =
    Prosumption(TypedActor.context.self, lamp.getProsumedPower(), new Date)

  def getStatus: ProsumerStatus = status
  
  def setStatus(status: ProsumerStatus) = this.status = status

  def tell(order: LoadBalancingOrder): LoadBalancingOrderResponse = {
    currentOrder = order
    Ack
  }
  
  var packet = EPacket(TypedActor.context.self, 3, 0, status)
  schedule.put(packet, schedule.now)
  
  def update = {
    isEconomising = status == Flexible && 
    		(currentOrder == ReduceLoad || schedule.getLoadAt(schedule.now) == ReduceLoad)
	if ( isEconomising ) {
	  if (lamp.getProsumedPower() == 0.0f) {
	    preEcoPower = 0.0f
	  } else if(lamp.getProsumedPower() < ecoMaxPower /* negative! */) { // high load, time to economise
	    preEcoPower = lamp.getProsumedPower()
	    lamp.setProsumedPower(ecoMaxPower)
	  }
	  
	} else if (!isEconomising && lamp.getProsumedPower() > preEcoPower) { // end of high load, restore power
	  lamp.setProsumedPower(preEcoPower)
	  preEcoPower = 0.0f
	}
    
    val newPacket = EPacket(TypedActor.context.self, 10, -lamp.getProsumedPower(), status)
    schedule.update(packet, newPacket)
    schedule.move(newPacket, schedule.now)
    packet = newPacket    
    
  }
  
  var isEconomising = status == Flexible && currentOrder == ReduceLoad
  var preEcoPower = 0.0f
  
}