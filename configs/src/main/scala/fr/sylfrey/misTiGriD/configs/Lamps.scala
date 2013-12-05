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
package fr.sylfrey.misTiGriD.configs.lamps

import java.util.{ List => JList, Map => JMap }
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import org.apache.felix.ipojo.annotations.Bind
import org.apache.felix.ipojo.annotations.Component
import org.apache.felix.ipojo.annotations.Instantiate
import org.apache.felix.ipojo.annotations.Validate
import org.apache.felix.ipojo.annotations.Invalidate
import org.apache.felix.ipojo.annotations.Requires
import org.apache.felix.ipojo.ComponentInstance
import scala.concurrent.{Future, ExecutionContext}
import fr.sylfrey.misTiGriD.deploy._
import org.osgi.framework.BundleContext
import fr.sylfrey.misTiGriD.wrappers.BundleContextProvider
import scala.concurrent.Promise

/**
 * Sample lamp configuration:
 * 5 smart lamps for the "Mouchez" house.
 */
@Component
@Instantiate(name="ConfigLamps")
class Config(  
  @Requires houseFactory : HouseFactory,
  @Requires metaFactory : MetaFactory,
  @Requires contextProvider: BundleContextProvider) {
  
  implicit lazy val ec = ExecutionContext.Implicits.global 
  
  lazy val instances = ListBuffer[ComponentInstance]()
    
  private def store(promise: Promise[ComponentInstance]): Unit = {
    promise.future onSuccess {
      case componentInstance => instances += componentInstance
    }
  }
  
  
  @Validate def start() = {
    
    // house level definitions, cf. "Mouchez" house configuration
    val aggregator = "houseAggregator"      
    val houseLoadManager = "houseLoadManager"
    val loadManagerURI = "akka://MisTiGriD/user/" + houseLoadManager
    
    // Lamp identificators.
    val lamp1 = "lamp_1"
    val lamp2 = "lamp_2"
    val lamp3 = "lamp_3"
    val lamp4 = "lamp_4"
    val lamp5 = "lamp_5"

    
    // Lamp (and lamp manager) specifications.
    val lamps = Map[String, Lamp](
      // lampID -> (maxPower, aggregator, loadManagerURI)  
      lamp1 -> Lamp(100, aggregator, loadManagerURI),
      lamp2 -> Lamp(100, aggregator, loadManagerURI),
      lamp3 -> Lamp(100, aggregator, loadManagerURI),
      lamp4 -> Lamp(100, aggregator, loadManagerURI),
      lamp5 -> Lamp(100, aggregator, loadManagerURI)
    )
    
    // Spatial organisation of lamps.
    val lampLayouts = Map[String, Tuple2[Int,Int]](
      // lampID -> (x, y)  
      lamp1 -> (240, 230),
      lamp2 -> (580, 330),
      lamp3 -> (720, 150),
      lamp4 -> (360, 760),
      lamp5 -> (610, 760)
    )
    
    lamps.keys foreach { name =>
      
      val lamp = lamps(name)
      store(houseFactory.makeLamp(name, lamp.maxPower, aggregator))
      store(houseFactory.makeLampManager(name, loadManagerURI))
      
      val (x,y) = lampLayouts(name)
      store(houseFactory.makeLampLayout(name, x, y))
      store(houseFactory.makeLampManagerLayout(name, x, y))
      
    }
    
  }
  
  @Invalidate def stop() : Unit = {
    instances.foreach( instance => { instance.stop; instance.dispose } )
    instances.clear
  }
    

}
