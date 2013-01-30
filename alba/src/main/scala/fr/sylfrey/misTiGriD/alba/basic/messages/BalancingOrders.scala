package fr.sylfrey.misTiGriD.alba.basic.messages

trait LoadBalancingOrder extends Serializable

object RiseLoad extends LoadBalancingOrder
object AnyLoad extends LoadBalancingOrder
object ReduceLoad extends LoadBalancingOrder


trait LoadBalancingOrderResponse extends Serializable

object Ack extends LoadBalancingOrderResponse