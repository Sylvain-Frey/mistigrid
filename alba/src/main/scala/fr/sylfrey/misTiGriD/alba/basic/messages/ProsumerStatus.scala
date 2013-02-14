package fr.sylfrey.misTiGriD.alba.basic.messages

trait ProsumerStatus

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

object Flexible extends ProsumerStatus
object SemiFlexible extends ProsumerStatus
object NonFlexible extends ProsumerStatus
