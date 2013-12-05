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
//package fr.sylfrey.misTiGriD.configs.district
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
//
//@Component
//@Instantiate
//class DistrictAggregator(  
//  @Requires houseFactory : HouseFactory,
//  @Requires metaFactory : MetaFactory,
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
//    val aggregator = "districtAggregator"
//    val districtLoadManager = "districtLoadManager"
//
//    spawn("Aggregator",
//      "instance.name" -> aggregator,
//      "actorPath" -> aggregator,
//      "hasRemoteParent" -> "false",
//      "remoteParentURL" -> "nil")
//
//    spawn("HouseLoadManagerDeployer",
//      "instance.name" -> districtLoadManager,
//      "actorPath" -> districtLoadManager,
//      "maxConsumption" -> "-3200",
//      "hysteresisThreshold" -> "500",
//      "loadReductionDelta" -> "500",
//      "acceptableLoadRange" -> "2000",
//      "prosumerStatus" -> "Flexible",
//      "period" -> "500",
//      "requires.from" -> metaFactory.&("aggregator" -> aggregator))
//      
//      
//    spawn("LoadManagerLayout",
//      "instance.name" -> (districtLoadManager + "Layout"),
//      "layout.name" -> districtLoadManager,
//      "x" -> "0",
//      "y" -> "0",
//      "width" -> "160",
//      "height" -> "120",
//      "layer" -> "10",
//      "requires.from" -> metaFactory.&("manager" -> districtLoadManager))
//      
//  }
//  
//  @Invalidate def stop(): Unit = {
//    instances.foreach(instance => { instance.stop; instance.dispose })
//    instances.clear
//  }
//
//}
