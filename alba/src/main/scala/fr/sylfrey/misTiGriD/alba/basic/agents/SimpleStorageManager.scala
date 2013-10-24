/*******************************************************************************
 * Copyright (c) 2013 Sylvain Frey.
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

import akka.actor.TypedActor
import fr.sylfrey.misTiGriD.alba.basic.messages.Ack
import fr.sylfrey.misTiGriD.alba.basic.messages.AnyLoad
import fr.sylfrey.misTiGriD.alba.basic.messages.Flexible
import fr.sylfrey.misTiGriD.alba.basic.messages.LoadBalancingOrder
import fr.sylfrey.misTiGriD.alba.basic.messages.LoadBalancingOrderResponse
import fr.sylfrey.misTiGriD.alba.basic.messages.ProsumerStatus
import fr.sylfrey.misTiGriD.alba.basic.messages.Prosumption
import fr.sylfrey.misTiGriD.alba.basic.messages.ReduceLoad
import fr.sylfrey.misTiGriD.alba.basic.model.EPacket
import fr.sylfrey.misTiGriD.alba.basic.model.Schedule
import fr.sylfrey.misTiGriD.alba.basic.roles.ProsumerManager
import fr.sylfrey.misTiGriD.electricalGrid.Storage

trait SimpleStorageManager extends ProsumerManager with Updatable

class SimpleStorageManagerAgent(
    storage: Storage,
    schedule: Schedule) extends SimpleStorageManager {

  var currentOrder: LoadBalancingOrder = AnyLoad

  def getProsumption(): Prosumption =
    Prosumption(TypedActor.context.self, storage.getProsumedPower(), new Date)

  def getStatus: ProsumerStatus = Flexible

  def setStatus(status: ProsumerStatus) = {}

  def tell(order: LoadBalancingOrder): LoadBalancingOrderResponse = {
    currentOrder = order
    Ack
  }

  var packet = EPacket(TypedActor.context.self, 3, 0, Flexible)
  schedule.put(packet, schedule.now)
  
  def update = {/*currentOrder match {
    case ReduceLoad				=> storage.setState(Storage.State.UNLOADING)
    case RiseLoad | AnyLoad 	=> storage.setState(Storage.State.LOADING)*/
    schedule.getLoadAt(schedule.now) match {
      case ReduceLoad => storage.setState(Storage.State.UNLOADING)
      case _ => storage.setState(Storage.State.LOADING)
    }
    val newPacket = EPacket(TypedActor.context.self, 3, storage.getProsumedPower(), Flexible)
    schedule.update(packet, newPacket)
    schedule.move(newPacket, schedule.now)
    packet = newPacket
  }

}
