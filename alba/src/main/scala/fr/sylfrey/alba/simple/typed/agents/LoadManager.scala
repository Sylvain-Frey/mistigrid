package fr.sylfrey.alba.simple.typed.agents
import akka.actor.ActorRef

trait LoadManager {
  def register(prosumer : ActorRef)
  def unregister(prosumer : ActorRef)
}