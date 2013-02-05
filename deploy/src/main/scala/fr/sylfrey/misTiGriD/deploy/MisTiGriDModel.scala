package fr.sylfrey.misTiGriD.deploy

import java.util.{ List => JList }

case class Atmosphere(
  name: String,
  temperature: Float,
  minTemperature: Float,
  maxTemperature: Float,
  isManual: Boolean)
  
case class Aggregator(
    name: String,
    actorPath: String,
    hasRemoteParent: Boolean,
    remoteParentURL: String)

case class Wall(
  surfacicHeatConductance: Float,
  isOpen: Boolean,
  openHeatConductance: Float,
  size: Float,
  neighbours: JList[String])

case class TH(temperature: Float,
  heatCapacity: Float,
  walls: JList[String])

case class Heater(
  prosumedPower: Float,
  heatConductance: Float,
  efficiency: Float,
  maxEmissionPower: Float,
  aggregator: String,
  room: String)

case class HeaterManager(
  actorPath: String,
  period: Int,
  requiredTemperature: Float,
  prosumerStatus: String,
  houseLoadManagerURI: String,
  heater: String,
  room: String,
  kp : Float,
  ki : Float,
  kd : Float	
  /*loadTopic: String,
  controller: String,
  hierarch: String,
  isCollaborative: Boolean*/)

case class Pos(x: Int, y: Int, layer: Int)

case class Dim(x: Int, y: Int, width: Int, height: Int, layer: Int)
