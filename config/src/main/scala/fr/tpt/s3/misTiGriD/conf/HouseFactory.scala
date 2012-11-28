package fr.tpt.s3.misTiGriD.conf

import java.util.{ List => JList }
import scala.collection.JavaConversions._
import org.apache.felix.ipojo.annotations.Instantiate
import org.apache.felix.ipojo.annotations.Component
import org.apache.felix.ipojo.annotations.Provides
import org.osgi.framework.BundleContext
import org.apache.felix.ipojo.annotations.Unbind
import org.apache.felix.ipojo.Factory
import org.apache.felix.ipojo.annotations.Bind
import org.osgi.framework.ServiceReference
import java.util.Dictionary
import java.util.HashMap
import org.apache.felix.ipojo.annotations.Validate

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
  isCollaborative: Boolean,
  loadTopic: String,
  controller: String,
  hierarch: String,
  heater: String,
  room: String)

case class Pos(x: Int, y: Int, layer: Int)

case class Dim(x: Int, y: Int, width: Int, height: Int, layer: Int)



trait HouseFactory /*extends MetaFactory*/ {
  
  def make(
    atmosphere: Atmosphere,
    aggregator: Aggregator,
    walls: Map[String, Wall],
    wallLayouts: Map[String, Dim],
    rooms: Map[String, TH],
    roomLayouts: Map[String, Dim],
    heaters: Map[String, Tuple2[Heater, HeaterManager]],
    heaterLayouts: Map[String, Tuple2[String,Pos]])
  
  def makeTH(name: String, th: TH): Unit

  def makeWall(name: String, wall: Wall): Unit

  def makeHeater(name: String, heater: Heater): Unit

  def makeHeaterManager(name: String, heaterManager: HeaterManager): Unit

  def makeTOLayout(name: String, dim: Dim, target: String): Unit

  def makeWallLayout(name: String, dim: Dim, wall: String): Unit

  def makeHeaterLayout(name: String, dim: Dim, heater: String): Unit

  def makeHeaterManagerLayout(name: String, dim: Dim, manager: String): Unit

  def makeTOView(name: String, target: String): Unit

  def makeHeaterView(name: String, heater: String): Unit

  def makeHeaterManagerView(name: String, manager: String, room: String): Unit

  def makeWallView(name: String, wall: String): Unit
  
  def &(items: (String, String)*): JList[Tuple2[String, Any]]
  
  def spawn(factoryName: String, items: (String, _)*): Unit
  
}

@Component
@Provides(specifications=Array(classOf[HouseFactory]))
@Instantiate
class HouseFactoryImpl extends /*MetaFactoryImpl with*/ HouseFactory {

//  @Bind(aggregate=true)
//  override def bind(factory: Factory) {
//    super.bind(factory)    
//  }
//
//  @Unbind
//  override def unbind(factory: Factory) {
//    super.unbind(factory)
//  }
  val factories = new HashMap[String, Factory]()
  var factRefs: Array[ServiceReference] = _
  val factorables = new HashMap[String, List[Dictionary[String, _]]]

  @Bind(aggregate=true)
  def bind(factory: Factory) {
    val factoryName = factory.getName()
    factories.put(factoryName, factory)
    // check for pending jobs
    if (factorables.containsKey(factoryName)) {
      factorables.remove(factoryName).foreach(factory.createComponentInstance(_))
    }
  }

  @Unbind
  def unbind(factory: Factory) {
    factories.remove(factory.getName())
  }

  def spawn(factoryName: String, items: (String, _)*): Unit = {
    if (factories.containsKey(factoryName)) { // factory available: call it
      factories.get(factoryName).createComponentInstance(parse(items))
    } else { // store the job for when factory becomes available
      if (factorables.containsKey(factoryName)) {
        factorables.put(factoryName, factorables.get(factoryName) ++ List(parse(items)))
      } else {
        factorables.put(factoryName, List(parse(items)))
      }
    }
  }

  def parse(items: JList[(String, _)]): Dictionary[String, _] = {
    val map = new HashMap[String, Any]()
    items.foreach(item => {
      item._2 match {
        case config: JList[Tuple2[String, Any]] => map.put(item._1, parse(config))
        case string: String => map.put(item._1, string)
        case erroneous => println("### skipping invalid configuration : " + erroneous)
      }
    })
    DictionaryWrapper(map)
  }

  def &(items: (String, String)*): JList[Tuple2[String, Any]] = {
    items.toList
  }
  

  @Validate
  def start() {
//    make(
//      House.atmosphereModel,
// 		House.aggregator,    
//      House.walls, House.wallLayouts,
//      House.rooms, House.roomLayouts,
//      House.heaters, House.heaterLayouts)
  }

  def make(
    atmosphere: Atmosphere,
    aggregator: Aggregator,
    walls: Map[String, Wall],
    wallLayouts: Map[String, Dim],
    rooms: Map[String, TH],
    roomLayouts: Map[String, Dim],
    heaters: Map[String, Tuple2[Heater, HeaterManager]],
    heaterLayouts: Map[String, Tuple2[String,Pos]]) {

    // model

    spawn("Atmosphere",
      "instance.name" -> atmosphere.name,
      "temperature" -> atmosphere.temperature.toString(),
      "minTemperature" -> atmosphere.minTemperature.toString(),
      "maxTemperature" -> atmosphere.maxTemperature.toString(),
      "isManual" -> atmosphere.isManual.toString(),
      "period" -> "50")
      
    spawn("Aggregator",
        "instance.name" -> aggregator.name,
        "actorPath" -> aggregator.actorPath,
        "hasRemoteParent" -> aggregator.hasRemoteParent.toString(),
        "remoteParentURL" -> aggregator.remoteParentURL)

    for ((name, wall) <- walls) {
      makeWall(name, wall)
    }

    for ((name, th) <- rooms) {
      makeTH(name, th)
    }

    for ((name, (heater, manager)) <- heaters) {
      makeHeater(name, heater)
      makeHeaterManager(name + "_manager", manager)
    }

    // layouts

    spawn("AtmosphereLayout",
        "instance.name" -> (atmosphere.name + "_layout"),
        "x" -> "0",
        "y" -> "0",
        "width" -> "1000",
        "height" -> "1000",
        "layer" -> "1",
        "requires.from" -> &("atmosphere" -> atmosphere.name))
            
      
    for ((room, dim) <- roomLayouts) {
      makeTOLayout(
        name = room + "_layout",
        dim = dim,
        target = room)
    }

    for ((heater, (room, pos)) <- heaterLayouts) {
      makeHeaterLayout(
        name = heater + "_layout",
        dim = Dim(pos.x, pos.y, 90, 50, pos.layer),
        heater = heater)
      makeHeaterManagerLayout(
        name = heater + "_manager_layout",
        dim = Dim(pos.x, pos.y - 50, 90, 50, pos.layer),
        manager = heater + "_manager")
    }

    for ((wall, dim) <- wallLayouts) {
      makeWallLayout(
        name = wall + "_layout",
        dim = dim,
        wall = wall)
    }

    // swing views

    makeTOView("atmosphereView", "atmosphere_layout")
    for (room <- roomLayouts.keys) makeTOView(room + "_view", room + "_layout")

    for ((heater, (room, pos)) <- heaterLayouts) {
      makeHeaterView(
        name = heater + "_view",
        heater = heater + "_layout")
      makeHeaterManagerView(
        name = heater + "_manager_view",
        manager = heater + "_manager_layout",
        room = room + "_layout")
    }

    for (wall <- wallLayouts.keys) {
      makeWallView(
        name = wall + "_view",
        wall = wall + "_layout")
    }

  }


  def makeTH(name: String, th: TH) = {
    spawn("ThermicObject",
      "instance.name" -> name,
      "temperature" -> th.temperature.toString(),
      "heatCapacity" -> th.heatCapacity.toString(),
      "period" -> "50",
      "requires.filters" -> &("walls" ->
        th.walls.mkString("(|(instance.name=", ")(instance.name=", "))")))
  }

  def makeWall(name: String, wall: Wall) {
    spawn("Wall",
      "instance.name" -> name,
      "surfacicHeatConductance" -> wall.surfacicHeatConductance.toString(),
      "isOpen" -> wall.isOpen.toString(),
      "openHeatConductance" -> wall.openHeatConductance.toString(),
      "size" -> wall.size.toString(),
      "requires.filters" -> &("thermicNeighbours" ->
        wall.neighbours.mkString("(|(instance.name=", ")(instance.name=", "))")))
  }

  def makeHeater(name: String, heater: Heater) {
    spawn("Heater",
      "instance.name" -> name,
      "prosumedPower" -> heater.prosumedPower.toString(),
      "heatConductance" -> heater.heatConductance.toString(),
      "efficiency" -> heater.efficiency.toString(),
      "maxEmissionPower" -> heater.maxEmissionPower.toString(),
      "requires.from" -> &(
        "aggregator" -> heater.aggregator,
        "room" -> heater.room))
  }

  def makeHeaterManager(name: String, heaterManager: HeaterManager) {
    spawn("MonolithicHeaterManager",
      "instance.name" -> name,
      "actorPath" -> heaterManager.actorPath,
      "period" -> heaterManager.period.toString(),
      "requiredTemperature" -> heaterManager.requiredTemperature.toString(),
      "isCollaborative" -> heaterManager.isCollaborative.toString(),
      "requires.from" -> &(
        "loadTopic" -> heaterManager.loadTopic,
        "controller" -> heaterManager.controller,
        "hierarch" -> heaterManager.hierarch,
        "heater" -> heaterManager.heater,
        "room" -> heaterManager.room))
  }

  def makeTOLayout(name: String, dim: Dim, target: String) = {
    spawn("ThermicObjectLayout",
      "instance.name" -> name,
      "x" -> dim.x.toString(),
      "y" -> dim.y.toString(),
      "width" -> dim.width.toString(),
      "height" -> dim.height.toString(),
      "layer" -> dim.layer.toString(),
      "requires.from" -> &("thermicObject" -> target))
  }

  def makeWallLayout(name: String, dim: Dim, wall: String) = {
    spawn("WallLayout",
      "instance.name" -> name,
      "x" -> dim.x.toString(),
      "y" -> dim.y.toString(),
      "width" -> dim.width.toString(),
      "height" -> dim.height.toString(),
      "layer" -> dim.layer.toString(),
      "requires.from" -> &("opening" -> wall))
  }

  def makeHeaterLayout(name: String, dim: Dim, heater: String) = {
    spawn("HeaterLayout",
      "instance.name" -> name,
      "x" -> dim.x.toString(),
      "y" -> dim.y.toString(),
      "width" -> dim.width.toString(),
      "height" -> dim.height.toString(),
      "layer" -> dim.layer.toString(),
      "requires.from" -> &("heater" -> heater))
  }

  def makeHeaterManagerLayout(name: String, dim: Dim, manager: String) = {
    spawn("HeaterManagerLayout",
      "instance.name" -> name,
      "x" -> dim.x.toString(),
      "y" -> dim.y.toString(),
      "width" -> dim.width.toString(),
      "height" -> dim.height.toString(),
      "layer" -> dim.layer.toString(),
      "requires.from" -> &("manager" -> manager))
  }

  def makeTOView(name: String, target: String) {
    spawn("ThermicObjectView",
      "instance.name" -> name,
      "period" -> "200",
      "requires.from" -> &("thermicObject" -> target))
  }

  def makeHeaterView(name: String, heater: String) {
    spawn("HeaterView",
      "instance.name" -> name,
      "period" -> "200",
      "requires.from" -> &("heater" -> heater))
  }

  def makeHeaterManagerView(name: String, manager: String, room: String) {
    spawn("HeaterManagerView",
      "instance.name" -> name,
      "period" -> "200",
      "requires.from" -> &("manager" -> manager, "room" -> room))
  }

  def makeWallView(name: String, wall: String) {
    spawn("WallView",
      "instance.name" -> name,
      "period" -> "200",
      "requires.from" -> &("opening" -> wall))
  }

}
