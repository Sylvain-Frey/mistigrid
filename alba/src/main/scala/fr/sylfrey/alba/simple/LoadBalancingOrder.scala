package fr.sylfrey.alba.simple

import akka.actor.ActorRef

trait LoadBalancingOrder

object RiseLoad extends LoadBalancingOrder
object AnyLoad extends LoadBalancingOrder
object ReduceLoad extends LoadBalancingOrder