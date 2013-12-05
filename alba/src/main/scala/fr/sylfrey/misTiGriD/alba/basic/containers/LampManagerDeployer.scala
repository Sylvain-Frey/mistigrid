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
package fr.sylfrey.misTiGriD.alba.basic.containers

import scala.Array.canBuildFrom
import scala.collection.JavaConversions.asJavaDictionary
import scala.collection.mutable.Map
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

import org.apache.felix.ipojo.annotations.Bind
import org.apache.felix.ipojo.annotations.Component
import org.apache.felix.ipojo.annotations.Property
import org.apache.felix.ipojo.annotations.Requires
import org.apache.felix.ipojo.annotations.Validate
import org.apache.felix.ipojo.annotations.Invalidate

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Cancellable
import akka.actor.TypedActor
import akka.actor.TypedProps

import fr.sylfrey.misTiGriD.wrappers.ActorSystemProvider
import fr.sylfrey.misTiGriD.alba.basic.agents.LampManager
import fr.sylfrey.misTiGriD.alba.basic.agents.LampManagerAgent
import fr.sylfrey.misTiGriD.alba.basic.roles.LoadManager
import fr.sylfrey.misTiGriD.alba.basic.roles.ProsumerManager
import fr.sylfrey.misTiGriD.electricalGrid.Lamp
import fr.sylfrey.misTiGriD.wrappers.BundleContextProvider
import fr.sylfrey.misTiGriD.alba.basic.messages.ProsumerStatus
import fr.sylfrey.misTiGriD.alba.basic.model.Schedule

@Component(name="LampManager", immediate=true)
class LampManagerDeployer {

  @Requires(id="lamp") var lamp : Lamp = _
  @Bind def bindActorSystem(asp : ActorSystemProvider)  { actorSystem = asp.getSystem() }
  @Requires var bundleContextProvider :BundleContextProvider = _
  @Requires var schedule: Schedule = _
  
  @Property(mandatory=true) var ecoMaxPower : Float = _
  @Property(mandatory=true) var prosumerStatus : String = _	
  @Property(mandatory=true) var period : Int = _
  @Property(mandatory=true) var actorPath : String = _
  @Property(mandatory=true) var houseLoadManagerURI : String = _
  
  @Validate def start() : Unit = {
    
    val status = ProsumerStatus.fromString(prosumerStatus)
    
    manager = TypedActor.get(actorSystem).typedActorOf(
	  TypedProps(
		  classOf[LampManager], 
          new LampManagerAgent(
              lamp = lamp, 
              status = status, 
              ecoMaxPower = ecoMaxPower,
              schedule = schedule)),
        actorPath)
    managerActorRef = TypedActor.get(actorSystem).getActorRefFor(manager)

    println("# getting houseLoadManager @ " + houseLoadManagerURI)
    houseLoadManager = TypedActor.get(actorSystem).typedActorOf(
      TypedProps[LoadManager](classOf[LoadManager]),
      actorSystem.actorFor(houseLoadManagerURI))
    houseLoadManager.register(managerActorRef)
		
    bundleContextProvider.get().registerService(
      Array( classOf[LampManager], classOf[ProsumerManager]).map( _.getName() ), 
      manager,
      Map("instance.name" -> actorPath, "service.pid" -> actorPath))
      
    implicit val executionContext = actorSystem.dispatcher
    periodicTask = actorSystem.scheduler.schedule(period milliseconds, period milliseconds) { manager.update }
    
  }
  
  @Invalidate def stop() : Unit = {
    periodicTask.cancel
    houseLoadManager.unregister(managerActorRef)
    TypedActor.get(actorSystem).stop(manager)
    TypedActor.get(actorSystem).stop(houseLoadManager)
  }
  
  private var manager : LampManager = _
  private var managerActorRef : ActorRef = _
  private var actorSystem : ActorSystem = _
  private var periodicTask : Cancellable = _
  private var houseLoadManager : LoadManager = _
  
}
