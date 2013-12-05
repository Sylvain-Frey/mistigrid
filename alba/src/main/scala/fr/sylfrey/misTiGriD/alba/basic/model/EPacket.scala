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
package fr.sylfrey.misTiGriD.alba.basic.model

import fr.sylfrey.misTiGriD.alba.basic.messages.ProsumerStatus
import akka.actor.ActorRef
import Types._
import java.util.UUID

object Types {

  type ID = ActorRef
  type T = Int
  type P = Float

}

case class EPacket(device: ID, length: T, max: P, status: ProsumerStatus) {
  
  val id = UUID.randomUUID()
  
  override def equals(that: Any): Boolean = {
    that.isInstanceOf[EPacket] && 
    that.asInstanceOf[EPacket].device == device &&
    that.asInstanceOf[EPacket].id == id
  }
  
  override def hashCode = device.hashCode + id.hashCode
  
}

