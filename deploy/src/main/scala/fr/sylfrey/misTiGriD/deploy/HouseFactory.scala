/*******************************************************************************
 * Copyright (c) 2013 EDF. This software was developed with the 
 * collaboration of Télécom ParisTech (Sylvain Frey).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Sylvain Frey - initial API and implementation
 ******************************************************************************/
package fr.sylfrey.misTiGriD.deploy

import java.util.{ List => JList }
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._
import scala.concurrent.{ promise, Promise, Future }
import org.apache.felix.ipojo.annotations.Instantiate
import org.apache.felix.ipojo.annotations.Component
import org.apache.felix.ipojo.annotations.Provides
import org.apache.felix.ipojo.annotations.Unbind
import org.apache.felix.ipojo.annotations.Requires
import org.apache.felix.ipojo.annotations.Validate
import org.apache.felix.ipojo.annotations.Bind
import org.apache.felix.ipojo.Factory
import org.apache.felix.ipojo.ComponentInstance
import org.osgi.framework.ServiceReference


/**
 * Type-safe wrapper for non-type-safe iPOJO factories.
 */
trait HouseFactory {

  def make(
    atmosphere: Atmosphere,
    aggregator: Aggregator,
    walls: Map[String, Wall],
    wallLayouts: Map[String, Dim],
    rooms: Map[String, ThermicObject],
    roomLayouts: Map[String, Dim],
    heaters: Map[String, (Heater, HeaterManager)],
    heaterLayouts: Map[String, (String, Pos)],
    lamps: Map[String, Lamp],
    lampLayouts: Map[String, (Int, Int)]): List[Promise[ComponentInstance]]
  
  def makeAtmosphere(atmosphere: Atmosphere): Promise[ComponentInstance]  
  def makeAggregator(aggregator: Aggregator): Promise[ComponentInstance]
  def makeThermicObject(name: String, th: ThermicObject): Promise[ComponentInstance]
  def makeWall(name: String, wall: Wall): Promise[ComponentInstance]
  def makeHeater(name: String, heater: Heater): Promise[ComponentInstance]
  def makeHeaterManager(name: String, heaterManager: HeaterManager): Promise[ComponentInstance]
  def makeLamp(name: String, maxPower: Int, aggregator: String): Promise[ComponentInstance]
  def makeLampManager(name: String, loadManagerURI: String): Promise[ComponentInstance]
  
  def makeThermicObjectLayout(name: String, dim: Dim, target: String): Promise[ComponentInstance]
  def makeWallLayout(name: String, dim: Dim, wall: String): Promise[ComponentInstance]
  def makeHeaterLayout(name: String, dim: Dim, heater: String): Promise[ComponentInstance]
  def makeHeaterManagerLayout(name: String, dim: Dim, manager: String): Promise[ComponentInstance]
  def makeAtmosphereLayout(atmosphere: Atmosphere): Promise[ComponentInstance]
  def makeLampLayout(name: String, x: Int, y: Int): Promise[ComponentInstance]
  def makeLampManagerLayout(name: String, x: Int, y: Int): Promise[ComponentInstance]
    
}

@Component
@Provides(specifications = Array(classOf[HouseFactory]))
@Instantiate
class HouseFactoryImpl(
    @Requires metaFactory: MetaFactory) extends HouseFactory {

  private def spawn(factoryName: String, items: (String, _)*): Promise[ComponentInstance] = {
    metaFactory.spawn(factoryName, items: _*)
  }
  private def &(items: (String, String)*): JList[Tuple2[String, Any]] = {
    metaFactory.&(items: _*)
  }

  def make(
    atmosphere: Atmosphere, 
    aggregator: Aggregator, 
    walls: Map[String, Wall], 
    wallLayouts: Map[String, Dim], 
    rooms: Map[String, ThermicObject], 
    roomLayouts: Map[String, Dim], 
    heaters: Map[String, (Heater, HeaterManager)], 
    heaterLayouts: Map[String, (String, Pos)], 
    lamps: Map[String, Lamp], 
    lampLayouts: Map[String, (Int, Int)]): List[Promise[ComponentInstance]] = {

    // list of future instances created by the house factory
    val result = ListBuffer[Promise[ComponentInstance]]()

    ///////////
    // model //
    ///////////

    result += makeAtmosphere(atmosphere)

    result += makeAggregator(aggregator)

    for ((name, wall) <- walls) {
      result += makeWall(name, wall)
    }

    for ((name, th) <- rooms) {
      result += makeThermicObject(name, th)
    }

    for ((name, (heater, manager)) <- heaters) {
      result += makeHeater(name, heater)
      result += makeHeaterManager(name + "_manager", manager)
    }
    
    for (name <- lamps.keys) {
      val lamp = lamps(name)
      result += makeLamp(name, lamp.maxPower, lamp.aggregator)
      result += makeLampManager(name, lamp.loadManagerURI)     
    }

    /////////////
    // layouts //
    /////////////

    result += makeAtmosphereLayout(atmosphere)

    for ((room, dim) <- roomLayouts) {
      result += makeThermicObjectLayout(
        name = room + "_layout",
        dim = dim,
        target = room)
    }

    for ((heater, (room, pos)) <- heaterLayouts) {
      result += makeHeaterLayout(
        name = heater + "_layout",
        dim = Dim(pos.x, pos.y, 150, 100, pos.layer),
        heater = heater)
      result += makeHeaterManagerLayout(
        name = heater + "_manager_layout",
        dim = Dim(pos.x + 20, pos.y - 90, 130, 100, pos.layer),
        manager = heater + "_manager")
    }

    for ((wall, dim) <- wallLayouts) {
      result += makeWallLayout(
        name = wall + "_layout",
        dim = dim,
        wall = wall)
    }
    
    for (lamp <- lampLayouts.keys) {
      val (x,y) = lampLayouts(lamp)
      result += makeLampLayout(lamp, x, y)
      result += makeLampManagerLayout(lamp, x, y)
    }
    
    result.toList

  }

  
  
  ///////////
  // model //
  ///////////

  
  def makeAtmosphere(atmosphere: Atmosphere) = {
    spawn("Atmosphere",
      "instance.name" -> atmosphere.name,
      "temperature" -> atmosphere.temperature.toString(),
      "minTemperature" -> atmosphere.minTemperature.toString(),
      "maxTemperature" -> atmosphere.maxTemperature.toString(),
      "isManual" -> atmosphere.isManual.toString(),
      "period" -> "50")
  }
    
  def makeAggregator(aggregator: Aggregator) = {
    spawn("Aggregator",
      "instance.name" -> aggregator.name,
      "actorPath" -> aggregator.actorPath,
      "hasRemoteParent" -> aggregator.hasRemoteParent.toString(),
      "remoteParentURL" -> aggregator.remoteParentURL)
  }
    
  def makeThermicObject(name: String, thermicObject: ThermicObject) = {
    spawn("ThermicObject",
      "instance.name" -> name,
      "temperature" -> thermicObject.temperature.toString(),
      "heatCapacity" -> thermicObject.heatCapacity.toString(),
      "period" -> "50",
      "requires.filters" -> &("walls" ->
        thermicObject.walls.mkString("(|(instance.name=", ")(instance.name=", "))")))
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
    spawn("BasicAlbaHeaterManager",
      "instance.name" -> name,
      "actorPath" -> heaterManager.actorPath,
      "period" -> heaterManager.period.toString(),
      "requiredTemperature" -> heaterManager.requiredTemperature.toString(),
      "prosumerStatus" -> heaterManager.prosumerStatus.toString(),
      "houseLoadManagerURI" -> heaterManager.houseLoadManagerURI,
      "kp" -> heaterManager.kp.toString(),
      "ki" -> heaterManager.ki.toString(),
      "kd" -> heaterManager.kd.toString(),
      "requires.from" -> &(
        "heater" -> heaterManager.heater,
        "room" -> heaterManager.room))
  }
    
  def makeLamp(name: String, maxPower: Int, aggregator: String) = {
      spawn("Lamp",
        "instance.name" -> name,
        "prosumedPower" -> "0",
        "maxEmissionPower" -> maxPower.toString,
        "requires.from" -> &("aggregator" -> aggregator))
  }
       
  def makeLampManager(name: String, loadManagerURI: String) = {
    spawn("LampManager",
        "instance.name" -> (name + "_manager"),          
        "ecoMaxPower" -> "-30",
        "prosumerStatus" -> "nonFlexible",
        "period" -> "500",
        "actorPath" -> (name + "_manager"),
        "houseLoadManagerURI" -> loadManagerURI,
        "requires.from" -> &("lamp" -> name))
  }    
  


  /////////////
  // layouts //
  /////////////
  
  def makeAtmosphereLayout(atmosphere: Atmosphere) = {
    spawn("AtmosphereLayout",
      "instance.name" -> (atmosphere.name + "_layout"),
      "x" -> "0",
      "y" -> "0",
      "width" -> "1000",
      "height" -> "1000",
      "layer" -> "1",
      "requires.from" -> &("atmosphere" -> atmosphere.name))
  }  

  def makeThermicObjectLayout(name: String, dim: Dim, target: String) = {
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
    
  def makeLampLayout(name: String, x: Int, y: Int) = {
    spawn("LampLayout",
        "instance.name" -> (name + "_layout"),
        "layout.name" -> (name + "_layout"),
        "x" -> x.toString,
        "y" -> y.toString,
        "width" -> "100",
        "height" -> "120",
        "layer" -> "10",
        "requires.from" -> &("lamp" -> name))
  }   
  
  def makeLampManagerLayout(name: String, x: Int, y: Int) = {
    spawn("LampManagerLayout",
        "instance.name" -> (name + "_manager_layout"),
        "layout.name" -> (name + "_mgr"),
        "x" -> (x-5).toString,
        "y" -> (y-60).toString,
        "width" -> "115",
        "height" -> "80",
        "layer" -> "10",          
        "requires.from" -> &("manager" -> (name + "_manager")))
  }  

}
