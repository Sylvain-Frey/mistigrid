package fr.sylfrey.misTiGriD.alba.basic.containers

import scala.Array.canBuildFrom
import scala.collection.JavaConversions.asJavaDictionary
import scala.collection.mutable.Map
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

import org.apache.felix.ipojo.annotations.Bind
import org.apache.felix.ipojo.annotations.Component
import org.apache.felix.ipojo.annotations.Invalidate
import org.apache.felix.ipojo.annotations.Property
import org.apache.felix.ipojo.annotations.Requires
import org.apache.felix.ipojo.annotations.Validate

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Cancellable
import akka.actor.TypedActor
import akka.actor.TypedProps

import fr.sylfrey.misTiGriD.alba.basic.agents.AlbaHeaterManager
import fr.sylfrey.misTiGriD.alba.basic.agents.HeaterManagerAgent
import fr.sylfrey.misTiGriD.alba.basic.agents.HouseLoadManager
import fr.sylfrey.misTiGriD.alba.basic.messages.ProsumerStatus
import fr.sylfrey.misTiGriD.alba.basic.model.Schedule
import fr.sylfrey.misTiGriD.alba.basic.roles.ProsumerManager
import fr.sylfrey.misTiGriD.appliances.Heater
import fr.sylfrey.misTiGriD.temperature.ThermicObject
import fr.sylfrey.misTiGriD.wrappers.BundleContextProvider
import fr.sylfrey.misTiGriD.wrappers.ActorSystemProvider

@Component(name="BasicAlbaHeaterManager",immediate=true)
class AlbaHeaterManagerDeployer {
    
  @Requires(id="room") var room : ThermicObject = _
  @Requires(id="heater") var heater : Heater = _ 
  @Requires var schedule: Schedule = _
  @Bind def bindActorSystem(asp : ActorSystemProvider)  { actorSystem = asp.getSystem() }
  @Requires var bundleContextProvider : BundleContextProvider = _
	
  @Property(mandatory=true) var requiredTemperature : Float = _	
  @Property(mandatory=true) var period : Int = _
  @Property(mandatory=true) var prosumerStatus : String = _
  @Property(mandatory=true) var actorPath : String = _
  @Property(mandatory=true) var houseLoadManagerURI : String = _
  @Property(mandatory=true) var kp : Float = _
  @Property(mandatory=true) var ki : Float = _
  @Property(mandatory=true) var kd : Float = _
	
  @Validate def start() : Unit = {
    
    val status = ProsumerStatus.fromString(prosumerStatus)
    
    manager = TypedActor.get(actorSystem).typedActorOf(
	  TypedProps(
		  classOf[AlbaHeaterManager], 
          new HeaterManagerAgent(
              heater = heater, 
              room = room, 
              status = status, 
              requiredTemperature = requiredTemperature,
              kp = kp, ki = ki, kd = kd,
              schedule = schedule)),
        actorPath)
    managerActorRef = TypedActor.get(actorSystem).getActorRefFor(manager)

    println("# getting houseLoadManager @ " + houseLoadManagerURI)
    houseLoadManager = TypedActor.get(actorSystem).typedActorOf(
      TypedProps[HouseLoadManager](classOf[HouseLoadManager]),
      actorSystem.actorFor(houseLoadManagerURI))
    houseLoadManager.register(managerActorRef)
		
    bundleContextProvider.get().registerService(
      Array( classOf[AlbaHeaterManager], classOf[ProsumerManager]).map( _.getName() ), 
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
	
  private var actorSystem : ActorSystem = _
  private var manager : AlbaHeaterManager = _
  private var managerActorRef : ActorRef = _
  private var houseLoadManager : HouseLoadManager = _
  private var periodicTask : Cancellable = _
	
}