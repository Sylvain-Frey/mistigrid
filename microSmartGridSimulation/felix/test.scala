import java.util.{ List => JList }
import scala.collection.JavaConversions._
import fr.tpt.s3.misTiGriD.conf._
import scala.collection.mutable.Map

import fr.tpt.s3.microSmartGridSimulation.management.Spawner
import fr.tpt.s3.microSmartGridSimulation.electricalGrid.Aggregator
import fr.tpt.s3.microSmartGridSimulation.management.resources.monolithicHeaterManager.MonolithicHeaterManager
import fr.tpt.s3.microSmartGridSimulation.management.resources.monolithicHeaterManager.MonolithicHeaterManagerImpl
import fr.tpt.s3.microSmartGridSimulation.management.resources.loadHierarch.LoadHierarch
import fr.tpt.s3.microSmartGridSimulation.management.resources.loadHierarch.LoadHierarchImpl
import fr.tpt.s3.microSmartGridSimulation.management.simpleAlba.SimpleAlbaHouseManager

val spawn = $[HouseFactory].spawn _
val & = $[HouseFactory].& _

spawn("Spawner")
val spawner = $[Spawner]

val aggregator = $[Aggregator]
val hierarch = new SimpleAlbaHouseManager
hierarch.aggregator = aggregator
hierarch.maxConsumption = 2600
hierarch.baseMaxConsumption = 2600

val hierarchActor = spawner.spawn(classOf[LoadHierarch], hierarch, "loadHierarch")
val props = Map("instance.name" -> "loadHierarch")
bundleContext.registerService(classOf[LoadHierarch].getName, hierarch, props)

