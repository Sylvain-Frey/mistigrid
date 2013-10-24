package fr.sylfrey.misTiGriD.deploy

import java.util.{ List => JList }

/**
 * Series of type-safe wrappers for 
 * non-type-safe iPOJO instance configurations.
 */

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

case class ThermicObject(temperature: Float,
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
  kd : Float)
  
case class Lamp(
    maxPower: Int,
    aggregator: String,
    loadManagerURI: String)

case class Pos(x: Int, y: Int, layer: Int)

case class Dim(x: Int, y: Int, width: Int, height: Int, layer: Int)
