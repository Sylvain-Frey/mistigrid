package fr.sylfrey.alba.simple
import akka.actor.ActorRef
import java.util.Date

case class Prosumption(prosumer : ActorRef, prosumption : Float, date : Date)

trait ProsumerStatus
object Flexible extends ProsumerStatus
object SemiFlexible extends ProsumerStatus
object NonFlexible extends ProsumerStatus
