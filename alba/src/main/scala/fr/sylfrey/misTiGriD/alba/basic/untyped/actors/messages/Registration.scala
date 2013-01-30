package fr.sylfrey.misTiGriD.alba.basic.untyped.actors.messages
import akka.actor.ActorRef

case class Register(prosumer : ActorRef)
case class Unregister(prosumer : ActorRef)