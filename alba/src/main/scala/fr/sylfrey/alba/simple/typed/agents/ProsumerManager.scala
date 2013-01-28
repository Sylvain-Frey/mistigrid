package fr.sylfrey.alba.simple.typed.agents

import fr.sylfrey.alba.simple.LoadBalancingOrder
import fr.sylfrey.alba.simple.ProsumerStatus
import fr.sylfrey.alba.simple.Prosumption

trait ProsumerManager {
  def getProsumption : Prosumption
  def getStatus : ProsumerStatus
  def tell(order : LoadBalancingOrder) : Unit
}