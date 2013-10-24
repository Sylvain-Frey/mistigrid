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
package fr.sylfrey.misTiGriD.alba.basic.messages

trait ProsumerStatus extends Serializable

object ProsumerStatus {
  def toString(status: ProsumerStatus): String = status match {
    case Flexible => "flexible"
    case SemiFlexible => "semiFlexible"
    case NonFlexible => "nonFlexible"
  }
  
  def fromString(status: String): ProsumerStatus = status.toLowerCase match {
    case "flexible" => Flexible
    case "semiflexible" => SemiFlexible 
    case "nonflexible" => NonFlexible
  }
}

object Flexible extends ProsumerStatus {
  override def toString = "flexible"
}
object SemiFlexible extends ProsumerStatus {
  override def toString = "semiFlexible"
}
object NonFlexible extends ProsumerStatus {
  override def toString = "nonFlexible"
}
