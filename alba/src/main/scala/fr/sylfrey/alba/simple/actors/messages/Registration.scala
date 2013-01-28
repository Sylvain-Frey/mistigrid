package fr.sylfrey.alba.simple.actors.messages
import akka.actor.ActorRef

case class Register(prosumer : ActorRef)
case class Unregister(prosumer : ActorRef)