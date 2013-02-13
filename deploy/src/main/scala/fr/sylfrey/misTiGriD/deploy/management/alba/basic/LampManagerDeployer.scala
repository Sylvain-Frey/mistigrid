package fr.sylfrey.misTiGriD.deploy.management.alba.basic

import scala.Array.canBuildFrom
import scala.collection.JavaConversions.asJavaDictionary
import scala.collection.mutable.Map
import scala.concurrent.duration.DurationInt

import org.apache.felix.ipojo.annotations.Bind
import org.apache.felix.ipojo.annotations.Component
import org.apache.felix.ipojo.annotations.Property
import org.apache.felix.ipojo.annotations.Requires
import org.apache.felix.ipojo.annotations.Validate

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Cancellable
import akka.actor.TypedActor
import akka.actor.TypedProps
import fr.sylfrey.akka.ActorSystemProvider
import fr.sylfrey.misTiGriD.alba.basic.agents.LampManager
import fr.sylfrey.misTiGriD.alba.basic.agents.LampManagerAgent
import fr.sylfrey.misTiGriD.alba.basic.roles.LoadManager
import fr.sylfrey.misTiGriD.alba.basic.roles.ProsumerManager
import fr.sylfrey.misTiGriD.deploy.management.alba.basic.StatusDecoder.decode
import fr.sylfrey.misTiGriD.electricalGrid.Lamp
import fr.sylfrey.misTiGriD.management.BundleContextProvider

@Component(name="LampManager", immediate=true)
class LampManagerDeployer {

  @Requires(id="lamp") var lamp : Lamp = _
  @Bind def bindActorSystem(asp : ActorSystemProvider)  { actorSystem = asp.getSystem() }
  @Requires var bundleContextProvider :BundleContextProvider = _
  
  @Property(mandatory=true) var ecoMaxPower : Float = _
  @Property(mandatory=true) var prosumerStatus : String = _	
  @Property(mandatory=true) var period : Int = _
  @Property(mandatory=true) var actorPath : String = _
  @Property(mandatory=true) var houseLoadManagerURI : String = _
  
  @Validate def start() : Unit = {
    
    val status = decode(prosumerStatus)
    
    manager = TypedActor.get(actorSystem).typedActorOf(
	  TypedProps(
		  classOf[LampManager], 
          new LampManagerAgent(
              lamp = lamp, 
              status = status, 
              ecoMaxPower = ecoMaxPower)),
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
  
  private var manager : LampManager = _
  private var managerActorRef : ActorRef = _
  private var actorSystem : ActorSystem = _
  private var periodicTask : Cancellable = _
  private var houseLoadManager : LoadManager = _
  
}