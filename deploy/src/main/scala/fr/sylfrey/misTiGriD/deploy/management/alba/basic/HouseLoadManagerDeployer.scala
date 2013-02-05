package fr.sylfrey.misTiGriD.deploy.management.alba.basic

import org.apache.felix.ipojo.annotations.Component
import scala.concurrent.duration._
import scala.collection.mutable.Map
import scala.collection.JavaConversions.asJavaDictionary
import fr.sylfrey.misTiGriD.alba.basic.messages.ProsumerStatus
import fr.sylfrey.misTiGriD.electricalGrid.Aggregator
import fr.sylfrey.misTiGriD.deploy.management.alba.basic.StatusDecoder.decode
import org.apache.felix.ipojo.annotations.Property
import org.apache.felix.ipojo.annotations.Validate
import org.apache.felix.ipojo.annotations.Requires
import fr.sylfrey.misTiGriD.alba.basic.agents.AlbaHeaterManager
import fr.sylfrey.akka.ActorSystemProvider
import akka.actor.Cancellable
import akka.actor.ActorSystem
import fr.sylfrey.misTiGriD.management.BundleContextProvider
import akka.actor.TypedActor
import fr.sylfrey.misTiGriD.alba.basic.roles.HouseLoadManager
import akka.actor.ActorRef
import akka.actor.TypedProps
import org.apache.felix.ipojo.annotations.Bind
import fr.sylfrey.misTiGriD.alba.basic.agents.ManageableHouseLoadManager
import fr.sylfrey.misTiGriD.alba.basic.agents.HouseLoadManagerAgent
import org.apache.felix.ipojo.annotations.Invalidate
import fr.sylfrey.misTiGriD.alba.basic.roles.HouseLoadManager
import fr.sylfrey.misTiGriD.alba.basic.roles.LoadManager

@Component(name = "HouseLoadManagerDeployer", immediate = true)
class HouseLoadManagerDeployer {

  @Requires(id = "aggregator") var aggregator: Aggregator = _
  @Bind def bindActorSystem(asp: ActorSystemProvider) { actorSystem = asp.getSystem() }
  @Requires var bundleContextProvider: BundleContextProvider = _

  @Property(mandatory = true) var maxConsumption: Float = _
  @Property(mandatory = true) var hysteresisThreshold: Float = _
  @Property(mandatory = true) var prosumerStatus: String = _
  @Property(mandatory = true) var period: Int = _
  @Property(mandatory = true) var actorPath: String = _
  @Property var hasParent: Boolean = _
  @Property var districtLoadManagerURI: String = _

  @Validate def start(): Unit = {
    val status = decode(prosumerStatus)

    houseLoadManager = TypedActor.get(actorSystem).typedActorOf(
      TypedProps(
        classOf[ManageableHouseLoadManager],
        new HouseLoadManagerAgent(aggregator, maxConsumption, hysteresisThreshold, status)),
      actorPath)
    managerActorRef = TypedActor.get(actorSystem).getActorRefFor(houseLoadManager)
    println("# houseLoadManager deployed : " + houseLoadManager)

    bundleContextProvider.get().registerService(
      Array(classOf[ManageableHouseLoadManager], classOf[HouseLoadManager]).map(_.getName()),
      houseLoadManager,
      Map("instance.name" -> actorPath, "service.pid" -> actorPath))

    if (hasParent) {
      districtLoadManager = TypedActor.get(actorSystem).typedActorOf(
        TypedProps[LoadManager](classOf[LoadManager]),
        actorSystem.actorFor(districtLoadManagerURI))
      districtLoadManager.register(managerActorRef)
    }

    implicit val executionContext = actorSystem.dispatcher
    periodicTask = actorSystem.scheduler.schedule(period milliseconds, period milliseconds) { houseLoadManager.update }

  }

  @Invalidate def stop(): Unit = {
    periodicTask.cancel
    if (hasParent) {
      districtLoadManager.unregister(managerActorRef)
      TypedActor.get(actorSystem).stop(districtLoadManager)
    }
    TypedActor.get(actorSystem).stop(houseLoadManager)
  }

  private var actorSystem: ActorSystem = _
  private var houseLoadManager: ManageableHouseLoadManager = _
  private var managerActorRef: ActorRef = _
  private var districtLoadManager: LoadManager = _
  private var periodicTask: Cancellable = _

}
