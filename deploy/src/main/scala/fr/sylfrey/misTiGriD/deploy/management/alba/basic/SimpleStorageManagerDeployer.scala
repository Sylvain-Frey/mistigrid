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
import org.apache.felix.ipojo.annotations.Invalidate
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Cancellable
import akka.actor.TypedActor
import akka.actor.TypedProps
import fr.sylfrey.akka.ActorSystemProvider
import fr.sylfrey.misTiGriD.electricalGrid.Lamp
import fr.sylfrey.misTiGriD.management.BundleContextProvider
import fr.sylfrey.misTiGriD.alba.basic.roles.LoadManager
import fr.sylfrey.misTiGriD.alba.basic.agents.LampManager
import fr.sylfrey.misTiGriD.electricalGrid.impl.SimpleStorage
import fr.sylfrey.misTiGriD.alba.basic.roles.ProsumerManager
import fr.sylfrey.misTiGriD.alba.basic.agents.LampManagerAgent
import fr.sylfrey.misTiGriD.alba.basic.agents.SimpleStorageManager
import fr.sylfrey.misTiGriD.alba.basic.agents.SimpleStorageManagerAgent
import fr.sylfrey.misTiGriD.electricalGrid.Storage


@Component(name="SimpleStorageManager", immediate=true)
class SimpleStorageManagerDeployer {
  
  @Requires(id="storage") var storage : Storage = _
  @Bind def bindActorSystem(asp : ActorSystemProvider)  { actorSystem = asp.getSystem() }
  @Requires var bundleContextProvider :BundleContextProvider = _
  	
  @Property(mandatory=true) var period : Int = _
  @Property(mandatory=true) var actorPath : String = _
  @Property(mandatory=true) var houseLoadManagerURI : String = _
  
  @Validate def start() : Unit = {
        
    manager = TypedActor.get(actorSystem).typedActorOf(
	  TypedProps(
		classOf[SimpleStorageManager], 
        new SimpleStorageManagerAgent(storage)),
      actorPath)
    managerActorRef = TypedActor.get(actorSystem).getActorRefFor(manager)

    println("# getting houseLoadManager @ " + houseLoadManagerURI)
    houseLoadManager = TypedActor.get(actorSystem).typedActorOf(
      TypedProps[LoadManager](classOf[LoadManager]),
      actorSystem.actorFor(houseLoadManagerURI))
    houseLoadManager.register(managerActorRef)
		
    bundleContextProvider.get().registerService(
      Array( classOf[SimpleStorageManager], classOf[ProsumerManager]).map( _.getName() ), 
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
  
  private var manager : SimpleStorageManager = _
  private var managerActorRef : ActorRef = _
  private var actorSystem : ActorSystem = _
  private var periodicTask : Cancellable = _
  private var houseLoadManager : LoadManager = _
  
}