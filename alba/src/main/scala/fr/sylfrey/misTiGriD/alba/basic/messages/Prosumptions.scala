package fr.sylfrey.misTiGriD.alba.basic.messages

import java.util.Date
import akka.actor.ActorRef

case class Prosumption(prosumer : ActorRef, prosumption : Float, date : Date)