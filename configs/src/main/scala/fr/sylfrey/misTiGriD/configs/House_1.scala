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
//package fr.sylfrey.misTiGriD.configs.house1
//
//import scala.collection.JavaConversions._
//import scala.collection.mutable.ListBuffer
//import org.apache.felix.ipojo.annotations.Bind
//import org.apache.felix.ipojo.annotations.Component
//import org.apache.felix.ipojo.annotations.Instantiate
//import org.apache.felix.ipojo.annotations.Validate
//import org.apache.felix.ipojo.annotations.Invalidate
//import org.apache.felix.ipojo.annotations.Requires
//import org.apache.felix.ipojo.ComponentInstance
//import scala.concurrent.{ Future, ExecutionContext }
//import fr.sylfrey.misTiGriD.deploy._
//import org.osgi.framework.BundleContext
//import fr.sylfrey.misTiGriD.deploy.BundleContextProvider
//
///**
// * Sample house configuration:
// * 3 rooms, 3 smart heaters, 3 smart lamps.
// */
//@Component
//@Instantiate
//class Config(
//  @Requires houseFactory: HouseFactory,
//  @Requires metaFactory: MetaFactory,
//  @Requires contextProvider: BundleContextProvider) {
//
//  implicit lazy val ec = ExecutionContext.Implicits.global
//
//  lazy val instances = ListBuffer[ComponentInstance]()
//
//  def spawn(factoryName: String, items: (String, Any)*): Unit = {
//    metaFactory.spawn(factoryName, items: _*).future onSuccess {
//      case componentInstance => instances += componentInstance
//    }
//  }
//
//  @Validate def start() = {
//
//    val context = contextProvider.get()
//
//    // House level definitions: atmosphere, electrical aggregators.
//    val atmosphere = "atmosphere"
//    val atmosphereModel = Atmosphere(name = atmosphere, isManual = true, temperature = 18.5f, minTemperature = 12, maxTemperature = 22)
//
//    val aggregator = "houseAggregator"
//    val hasRemoteParent = context.getProperty("MisTiGriD.hasRemoteParent") match {
//      case "true" => true
//      case "false" => false
//      case incorrect => println("### invalid configuration: " + incorrect); false
//    }
//    val remoteParentURL = context.getProperty("MisTiGriD.remoteParentURL")
//    // typical value: "akka://MisTiGriD@host:4004/user/districtAggregator"
//    // cf. config.properties
//    val aggregatorModel = Aggregator(
//      name = aggregator,
//      actorPath = aggregator,
//      hasRemoteParent = hasRemoteParent,
//      remoteParentURL = remoteParentURL)
//
//    val aggregatorController = "houseAggregatorController"
//    val loadTopic = "houseLoadTopic"
//    val houseLoadManager = "houseLoadManager"
//    val loadManagerURI = "akka://MisTiGriD/user/" + houseLoadManager
//    val districtLoadManagerURI = context.getProperty("MisTiGriD.districtLoadManagerURI")
//    // typical value: "akka://MisTiGriD@localhost:4004/user/districtLoadManager"
//    // cf. config.properties         
//
//    ////////////////////
//    // IDENTIFICATORS //
//    ////////////////////
//
//    // Room identificators.
//
//    val bathroom = "bathroom"
//    val room = "room"
//    val livingroom = "livingroom"
//
//    val arn = "wall_arn"
//    val arw = "wall_arw"
//    val ars = "wall_ars"
//    val abn = "wall_abn"
//    val abe = "wall_abe"
//    val ale = "wall_ale"
//    val als = "wall_als"
//    val rb = "wall_rb"
//    val rl = "wall_rl"
//    val bl = "wall_bl"
//
//    // Heater identificators.
//    val heaterLivingroom = "heater_" + livingroom
//    val heaterRoom = "heater_" + room
//    val heaterBathroom = "heater_" + bathroom
//
//    // Lamp identificators.
//    val lamp1 = "lamp_1"
//    val lamp2 = "lamp_2"
//    val lamp3 = "lamp_3"
//
//    ////////////////////
//    // SPECIFICATIONS //
//    ////////////////////      
//
//    // Wall specifications.
//    val walls = Map[String, Wall](
//      // wallID -> Wall(surfacicHeatConductance, openness, openHeatConductance, length, list of neighbours)
//      //val walls = Map[String, Wall](
//      arn -> Wall(0.01f, false, 0.05f, 3, List(atmosphere, room)),
//      arw -> Wall(0.01f, false, 0.05f, 2, List(atmosphere, room)),
//      ars -> Wall(0.01f, false, 0.05f, 4, List(atmosphere, room)),
//      als -> Wall(0.01f, false, 0.05f, 5, List(atmosphere, livingroom)),
//      ale -> Wall(0.01f, false, 0.05f, 4, List(atmosphere, livingroom)),
//      abe -> Wall(0.01f, false, 0.05f, 9, List(atmosphere, bathroom)),
//      abn -> Wall(0.01f, false, 0.05f, 3, List(atmosphere, bathroom)),
//      rb ->  Wall(0.01f, false, 0.05f, 4, List(room, bathroom)),
//      rl ->  Wall(0.01f, false, 0.05f, 3, List(room, livingroom)),
//      bl ->  Wall(0.01f, false, 0.05f, 3, List(bathroom, livingroom)))
//
//    // Room specifications.
//    val rooms = Map[String, ThermicObject](
//      // roomID -> ThermicObject(initialTemperature, heatCapacity, list of walls and heaters)
//      room -> ThermicObject(24, 8, List(arn, arw, ars, rb, rl, heaterRoom)),
//      bathroom -> ThermicObject(24, 5, List(abn, abe, rb, bl, heaterBathroom)),
//      livingroom -> ThermicObject(24, 15, List(als, ale, rl, bl, heaterLivingroom)))
//
//    // Heater (and heater manager) specifications.
//    val heaters = Map[String, Tuple2[Heater, HeaterManager]](
//      // heaterID -> ...
//      heaterRoom -> Tuple2(
//        Heater(0, 0.02f, 0.1f, 200f, aggregator, room),
//        HeaterManager(
//          actorPath = heaterRoom + "_manager",
//          period = 400,
//          requiredTemperature = 22,
//          prosumerStatus = "Flexible",
//          houseLoadManagerURI = loadManagerURI,
//          heater = heaterRoom,
//          room = room,
//          kp = 40, ki = 0, kd = 0)),
//      heaterLivingroom -> Tuple2(
//        Heater(0, 0.02f, 0.1f, 200f, aggregator, livingroom),
//        HeaterManager(
//          actorPath = heaterLivingroom + "_manager",
//          period = 400,
//          requiredTemperature = 22,
//          prosumerStatus = "Flexible",
//          houseLoadManagerURI = loadManagerURI,
//          heater = heaterLivingroom,
//          room = livingroom,
//          kp = 40, ki = 0f, kd = 0)),
//      heaterBathroom -> Tuple2(
//        Heater(0, 0.02f, 0.1f, 200f, aggregator, bathroom),
//        HeaterManager(
//          actorPath = heaterBathroom + "_manager",
//          period = 400,
//          requiredTemperature = 22,
//          prosumerStatus = "Flexible",
//          houseLoadManagerURI = loadManagerURI,
//          heater = heaterBathroom,
//          room = bathroom,
//          kp = 40, ki = 0, kd = 0)))
//
//    // Lamp specifications.
//    val lamps = Map[String, Lamp](
//      // lampID -> (maxPower, loadManagerURI)  
//      lamp1 -> Lamp(100, aggregator, loadManagerURI),
//      lamp2 -> Lamp(100, aggregator, loadManagerURI),
//      lamp3 -> Lamp(100, aggregator, loadManagerURI))
//
//    /////////////
//    // LAYOUTS //
//    /////////////
//
//    // Spatial organisation of rooms.
//    val roomLayouts = Map[String, Dim](
//      // roomID -> Dim(x, y, width, height, z)
//      bathroom -> 	Dim(350, 50, 600, 200, 5),
//      room -> 		Dim(50, 50, 300, 600, 5),
//      livingroom -> Dim(350, 250, 600, 400, 3))
//
//    // Spatial organisation of heaters.
//    val heaterLayouts = Map[String, Tuple2[String, Pos]](
//      // heaterID -> (roomID, Pos(x, y, z))
//      heaterBathroom -> 	(bathroom, 		Pos(700, 120, 10)),
//      heaterRoom -> 		(room, 			Pos(80, 220, 10)),
//      heaterLivingroom -> 	(livingroom, 	Pos(700, 400, 10)))
//
//    // Spatial organisation of doors (not intuitive, I know).
//    val wallLayouts = Map[String, Dim](
//      // wallID -> Dim(x, y, width, height, z)
//      rl -> 	Dim(350, 300, 10, 80, 6),
//      bl -> 	Dim(500, 250, 80, 10, 6),
//      als -> 	Dim(500, 650, 80, 10, 6))
//
//    // Spatial organisation of lamps.
//    val lampLayouts = Map[String, Tuple2[Int, Int]](
//      // lampID -> (x, y)  
//      lamp1 -> (100, 450),
//      lamp2 -> (500, 100),
//      lamp3 -> (500, 400))
//
//    houseFactory.make(
//      atmosphereModel,
//      aggregatorModel,
//      walls, wallLayouts,
//      rooms, roomLayouts,
//      heaters, heaterLayouts,
//      lamps, lampLayouts).foreach(_.future onSuccess {
//        case componentInstance => instances += componentInstance
//      })
//
//    // Load manager specification and layout.
//
//    spawn("HouseLoadManagerDeployer",
//      "instance.name" -> houseLoadManager,
//      "actorPath" -> houseLoadManager,
//      "maxConsumption" -> "-600",
//      "hysteresisThreshold" -> "300",
//      "loadReductionDelta" -> "300",
//      "acceptableLoadRange" -> "1000",
//      "prosumerStatus" -> "Flexible",
//      "period" -> "50",
//      "hasParent" -> hasRemoteParent.toString,
//      "districtLoadManagerURI" -> districtLoadManagerURI,
//      "requires.from" -> metaFactory.&("aggregator" -> aggregator))
//
//    spawn("LoadManagerLayout",
//      "instance.name" -> (houseLoadManager + "Layout"),
//      "layout.name" -> houseLoadManager,
//      "x" -> "0",
//      "y" -> "0",
//      "width" -> "160",
//      "height" -> "120",
//      "layer" -> "10",
//      "requires.from" -> metaFactory.&("manager" -> houseLoadManager))
//
//  }
//
//  @Invalidate def stop(): Unit = {
//    instances.foreach(instance => { instance.stop; instance.dispose })
//    instances.clear
//  }
//
//}
