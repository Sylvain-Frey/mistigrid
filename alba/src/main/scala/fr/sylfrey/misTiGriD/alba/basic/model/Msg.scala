package fr.sylfrey.misTiGriD.alba.basic.model

import fr.sylfrey.misTiGriD.alba.basic.model.Types.T

case object Tick

case object Print

case class New(packet: EPacket, start: T)

case class ReduceLoad(start: T, end: T)