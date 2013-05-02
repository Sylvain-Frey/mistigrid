package fr.sylfrey.misTiGriD.deploy.management.alba.basic

import scala.Array.canBuildFrom
import scala.collection.JavaConversions.asJavaDictionary
import scala.collection.mutable.Map
import scala.concurrent.duration.DurationInt
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
import fr.sylfrey.akka.ActorSystemProvider
import fr.sylfrey.misTiGriD.alba.basic.agents.HouseLoadManager
import fr.sylfrey.misTiGriD.alba.basic.agents.HouseLoadManagerAgent
import fr.sylfrey.misTiGriD.alba.basic.roles.LoadManager
import fr.sylfrey.misTiGriD.electricalGrid.Aggregator
import fr.sylfrey.misTiGriD.management.BundleContextProvider
import fr.sylfrey.misTiGriD.alba.basic.messages.ProsumerStatus
import fr.sylfrey.misTiGriD.alba.basic.model.Schedule

@Component(name = "HouseLoadManagerDeployer", immediate = true)
class HouseLoadManagerDeployer {

  @Requires(id = "aggregator") var aggregator: Aggregator = _
  @Bind def bindActorSystem(asp: ActorSystemProvider) { actorSystem = asp.getSystem() }
  @Requires var bundleContextProvider: BundleContextProvider = _
  @Requires var schedule: Schedule = _

  @Property(mandatory = true) var maxConsumption: Float = _
  @Property(mandatory = true) var hysteresisThreshold: Float = _
  @Property(mandatory = true) var prosumerStatus: String = _
  @Property(mandatory = true) var period: Int = _
  @Property(mandatory = true) var actorPath: String = _
  @Property var hasParent: Boolean = _
  @Property var districtLoadManagerURI: String = _

  @Validate def start(): Unit = {
    val status = ProsumerStatus.fromString(prosumerStatus)
    val fatherSchedule = hasParent match {
      case false => None
      case true =>
        // remoteScheduleURI = districtLoadManagerURI/../schedule. Yuck.
        println("# districtLoadManagerURI = " + districtLoadManagerURI)
        val prefixLength = districtLoadManagerURI.lastIndexOf("/")
        val prefix = districtLoadManagerURI.substring(0, prefixLength)
        val remoteScheduleURI = prefix + "/schedule"
        println("# trying to connect to remote schedule @ " + remoteScheduleURI)
        val schedule = TypedActor.get(actorSystem).typedActorOf(
          TypedProps[Schedule](classOf[Schedule]),
          actorSystem.actorFor(remoteScheduleURI))
        println("# connected to " + remoteScheduleURI +" of size " + schedule.size)
        Some(schedule)
    }

    houseLoadManager = TypedActor.get(actorSystem).typedActorOf(
      TypedProps(
        classOf[HouseLoadManager],
        new HouseLoadManagerAgent(
          aggregator,
          maxConsumption,
          hysteresisThreshold,
          status,
          schedule,
          fatherSchedule)),
      actorPath)
    managerActorRef = TypedActor.get(actorSystem).getActorRefFor(houseLoadManager)
    println("# houseLoadManager deployed : " + houseLoadManager)

    bundleContextProvider.get().registerService(
      Array(classOf[HouseLoadManager]).map(_.getName()),
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
  private var houseLoadManager: HouseLoadManager = _
  private var managerActorRef: ActorRef = _
  private var districtLoadManager: LoadManager = _
  private var periodicTask: Cancellable = _

}
