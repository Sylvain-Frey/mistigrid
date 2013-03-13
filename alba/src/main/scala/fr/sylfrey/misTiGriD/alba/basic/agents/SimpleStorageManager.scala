package fr.sylfrey.misTiGriD.alba.basic.agents

import java.util.Date

import akka.actor.TypedActor
import fr.sylfrey.misTiGriD.alba.basic.messages.Ack
import fr.sylfrey.misTiGriD.alba.basic.messages.AnyLoad
import fr.sylfrey.misTiGriD.alba.basic.messages.Flexible
import fr.sylfrey.misTiGriD.alba.basic.messages.LoadBalancingOrder
import fr.sylfrey.misTiGriD.alba.basic.messages.LoadBalancingOrderResponse
import fr.sylfrey.misTiGriD.alba.basic.messages.ProsumerStatus
import fr.sylfrey.misTiGriD.alba.basic.messages.Prosumption
import fr.sylfrey.misTiGriD.alba.basic.messages.ReduceLoad
import fr.sylfrey.misTiGriD.alba.basic.messages.RiseLoad
import fr.sylfrey.misTiGriD.alba.basic.roles.ProsumerManager
import fr.sylfrey.misTiGriD.electricalGrid.Storage

trait SimpleStorageManager extends ProsumerManager with Updatable

class SimpleStorageManagerAgent(storage: Storage) extends SimpleStorageManager {

  var currentOrder: LoadBalancingOrder = AnyLoad

  def getProsumption(): Prosumption =
    Prosumption(TypedActor.context.self, storage.getProsumedPower(), new Date)

  def getStatus: ProsumerStatus = Flexible

  def setStatus(status: ProsumerStatus) = {}

  def tell(order: LoadBalancingOrder): LoadBalancingOrderResponse = {
    currentOrder = order
    Ack
  }

  def update = currentOrder match {
    case ReduceLoad				=> storage.setState(Storage.State.UNLOADING)
    case RiseLoad | AnyLoad 	=> storage.setState(Storage.State.LOADING)
  }

}