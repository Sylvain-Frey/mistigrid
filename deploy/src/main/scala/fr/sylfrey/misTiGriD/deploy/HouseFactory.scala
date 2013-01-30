package fr.sylfrey.misTiGriD.deploy

import java.util.{ List => JList }
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._
import org.apache.felix.ipojo.annotations.Instantiate
import org.apache.felix.ipojo.annotations.Component
import org.apache.felix.ipojo.annotations.Provides
import org.apache.felix.ipojo.annotations.Unbind
import org.apache.felix.ipojo.Factory
import org.apache.felix.ipojo.annotations.Bind
import org.osgi.framework.ServiceReference
import java.util.Dictionary
import java.util.HashMap
import org.apache.felix.ipojo.annotations.Validate
import scala.concurrent.{promise, Promise, Future}
import org.apache.felix.ipojo.ComponentInstance

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
  room: String
  /*loadTopic: String,
  controller: String,
  hierarch: String,
  isCollaborative: Boolean*/)

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
    heaterLayouts: Map[String, Tuple2[String,Pos]]) : List[Promise[ComponentInstance]]
  
  def makeTH(name: String, th: TH): Promise[ComponentInstance]

  def makeWall(name: String, wall: Wall): Promise[ComponentInstance]

  def makeHeater(name: String, heater: Heater): Promise[ComponentInstance]

  def makeHeaterManager(name: String, heaterManager: HeaterManager): Promise[ComponentInstance]

  def makeTOLayout(name: String, dim: Dim, target: String): Promise[ComponentInstance]

  def makeWallLayout(name: String, dim: Dim, wall: String): Promise[ComponentInstance]

  def makeHeaterLayout(name: String, dim: Dim, heater: String): Promise[ComponentInstance]

  def makeHeaterManagerLayout(name: String, dim: Dim, manager: String): Promise[ComponentInstance]

  def makeTOView(name: String, target: String): Promise[ComponentInstance]

  def makeHeaterView(name: String, heater: String): Promise[ComponentInstance]

  def makeHeaterManagerView(name: String, manager: String, room: String): Promise[ComponentInstance]

  def makeWallView(name: String, wall: String): Promise[ComponentInstance]
  
  def &(items: (String, String)*): JList[Tuple2[String, Any]]
  
  def spawn(factoryName: String, items: (String, _)*) : Promise[ComponentInstance]
  
}

@Component
@Provides(specifications=Array(classOf[HouseFactory]))
@Instantiate
class HouseFactoryImpl extends HouseFactory {

  val factories = new HashMap[String, Factory]()
  var factRefs: Array[ServiceReference] = _
  val factorables = new HashMap[String, List[Tuple2[Promise[ComponentInstance], Dictionary[String, _]]] ]

  @Bind(aggregate=true)
  def bind(factory: Factory) {
    val factoryName = factory.getName()
    factories.put(factoryName, factory)
    // check for pending jobs
    if (factorables.containsKey(factoryName)) {
    	factorables.remove(factoryName).foreach( _ match {
    	  case (promise, config) => promise success factory.createComponentInstance(config)
    	})
    }
  }

  @Unbind
  def unbind(factory: Factory) {
    factories.remove(factory.getName())
  }

  def spawn(factoryName: String, items: (String, _)*) : Promise[ComponentInstance] = {
    val p = promise[ComponentInstance]
    if (factories.containsKey(factoryName)) { // factory available: call it
      p success factories.get(factoryName).createComponentInstance(parse(items))
    } else { // store the job for when factory becomes available
      val job = Tuple2(p, parse(items))
      if (factorables.containsKey(factoryName)) {
        factorables.put(factoryName, job +: factorables.get(factoryName))
      } else {
        factorables.put(factoryName, List(job) )
      }
    }
    p
  }

  def parse(items: JList[(String, _)]): Dictionary[String, _] = {
    val map = new HashMap[String, Any]()
    items.foreach { case (key, value) => value match {
        case config: JList[Tuple2[String, Any]] => map.put(key, parse(config))
        case string: String => map.put(key, string)
        case erroneous => println("### skipping invalid configuration : " + erroneous)
      }
    }
    DictionaryWrapper(map)
  }

  def &(items: (String, String)*): JList[Tuple2[String, Any]] = {
    items.toList
  }
  


  def make(
    atmosphere: Atmosphere,
    aggregator: Aggregator,
    walls: Map[String, Wall],
    wallLayouts: Map[String, Dim],
    rooms: Map[String, TH],
    roomLayouts: Map[String, Dim],
    heaters: Map[String, Tuple2[Heater, HeaterManager]],
    heaterLayouts: Map[String, Tuple2[String,Pos]]) : List[Promise[ComponentInstance]] = {

    
    val result = ListBuffer[Promise[ComponentInstance]]()
    
    // model

    result += spawn("Atmosphere",
      "instance.name" -> atmosphere.name,
      "temperature" -> atmosphere.temperature.toString(),
      "minTemperature" -> atmosphere.minTemperature.toString(),
      "maxTemperature" -> atmosphere.maxTemperature.toString(),
      "isManual" -> atmosphere.isManual.toString(),
      "period" -> "50")
      
    result += spawn("Aggregator",
        "instance.name" -> aggregator.name,
        "actorPath" -> aggregator.actorPath,
        "hasRemoteParent" -> aggregator.hasRemoteParent.toString(),
        "remoteParentURL" -> aggregator.remoteParentURL)

    for ((name, wall) <- walls) {
      result += makeWall(name, wall)
    }

    for ((name, th) <- rooms) {
      result += makeTH(name, th)
    }

    for ((name, (heater, manager)) <- heaters) {
      result += makeHeater(name, heater)
      result += makeHeaterManager(name + "_manager", manager)
    }

    // layouts

    result += spawn("AtmosphereLayout",
        "instance.name" -> (atmosphere.name + "_layout"),
        "x" -> "0",
        "y" -> "0",
        "width" -> "1000",
        "height" -> "1000",
        "layer" -> "1",
        "requires.from" -> &("atmosphere" -> atmosphere.name))
            
      
    for ((room, dim) <- roomLayouts) {
      result += makeTOLayout(
        name = room + "_layout",
        dim = dim,
        target = room)
    }

    for ((heater, (room, pos)) <- heaterLayouts) {
      result += makeHeaterLayout(
        name = heater + "_layout",
        dim = Dim(pos.x, pos.y, 90, 50, pos.layer),
        heater = heater)
      result += makeHeaterManagerLayout(
        name = heater + "_manager_layout",
        dim = Dim(pos.x, pos.y - 50, 90, 50, pos.layer),
        manager = heater + "_manager")
    }

    for ((wall, dim) <- wallLayouts) {
      result += makeWallLayout(
        name = wall + "_layout",
        dim = dim,
        wall = wall)
    }

    // swing views

    result += makeTOView("atmosphereView", "atmosphere_layout")
    for (room <- roomLayouts.keys) result += makeTOView(room + "_view", room + "_layout")

    for ((heater, (room, pos)) <- heaterLayouts) {
      result += makeHeaterView(
        name = heater + "_view",
        heater = heater + "_layout")
      result += makeHeaterManagerView(
        name = heater + "_manager_view",
        manager = heater + "_manager_layout",
        room = room + "_layout")
    }

    for (wall <- wallLayouts.keys) {
      result += makeWallView(
        name = wall + "_view",
        wall = wall + "_layout")
    }
    
    result.toList

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

  def makeWall(name: String, wall: Wall) = {
    spawn("Wall",
      "instance.name" -> name,
      "surfacicHeatConductance" -> wall.surfacicHeatConductance.toString(),
      "isOpen" -> wall.isOpen.toString(),
      "openHeatConductance" -> wall.openHeatConductance.toString(),
      "size" -> wall.size.toString(),
      "requires.filters" -> &("thermicNeighbours" ->
        wall.neighbours.mkString("(|(instance.name=", ")(instance.name=", "))")))
  }

  def makeHeater(name: String, heater: Heater) = {
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

  def makeHeaterManager(name: String, heaterManager: HeaterManager) = {
//    spawn("MonolithicHeaterManager",
//      "instance.name" -> name,
//      "actorPath" -> heaterManager.actorPath,
//      "period" -> heaterManager.period.toString(),
//      "requiredTemperature" -> heaterManager.requiredTemperature.toString(),
//      "isCollaborative" -> heaterManager.isCollaborative.toString(),
//      "requires.from" -> &(
//        "loadTopic" -> heaterManager.loadTopic,
//        "controller" -> heaterManager.controller,
//        "hierarch" -> heaterManager.hierarch,
//        "heater" -> heaterManager.heater,
//        "room" -> heaterManager.room))
    spawn("BasicAlbaHeaterManager",
      "instance.name" -> name,
      "actorPath" -> heaterManager.actorPath,
      "period" -> heaterManager.period.toString(),
      "requiredTemperature" -> heaterManager.requiredTemperature.toString(),
      "prosumerStatus" -> heaterManager.prosumerStatus.toString(),
      "houseLoadManagerURI" -> heaterManager.houseLoadManagerURI,
      "requires.from" -> &(
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

  def makeTOView(name: String, target: String) = {
    spawn("ThermicObjectView",
      "instance.name" -> name,
      "period" -> "200",
      "requires.from" -> &("thermicObject" -> target))
  }

  def makeHeaterView(name: String, heater: String) = {
    spawn("HeaterView",
      "instance.name" -> name,
      "period" -> "200",
      "requires.from" -> &("heater" -> heater))
  }

  def makeHeaterManagerView(name: String, manager: String, room: String) = {
    spawn("HeaterManagerView",
      "instance.name" -> name,
      "period" -> "200",
      "requires.from" -> &("manager" -> manager, "room" -> room))
  }

  def makeWallView(name: String, wall: String) = {
    spawn("WallView",
      "instance.name" -> name,
      "period" -> "200",
      "requires.from" -> &("opening" -> wall))
  }

}
