package fr.sylfrey.misTiGriD.configs

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import org.apache.felix.ipojo.annotations.Bind
import org.apache.felix.ipojo.annotations.Component
import org.apache.felix.ipojo.annotations.Instantiate
import org.apache.felix.ipojo.annotations.Validate
import org.apache.felix.ipojo.annotations.Invalidate
import org.apache.felix.ipojo.annotations.Requires
import org.apache.felix.ipojo.ComponentInstance
import scala.concurrent.{ Future, ExecutionContext }
import fr.sylfrey.misTiGriD.deploy._

//@Component
////@Instantiate
//class DistrictAggregator {
//
//  @Requires var houseFactory: HouseFactory = null
//  @Requires var metaFactory: MetaFactory = null
//  implicit val ec = ExecutionContext.Implicits.global
//
//  @Validate def start() = {
//
//    val aggregator = "districtAggregator"
//    val districtLoadManager = "districtLoadManager"
//
//    spawn("RemoteAggregator",
//      "instance.name" -> aggregator,
//      "actorPath" -> aggregator,
//      "hasRemoteParent" -> "false",
//      "remoteParentURL" -> "nil")
//
//    spawn("HouseLoadManagerDeployer",
//      "instance.name" -> districtLoadManager,
//      "actorPath" -> districtLoadManager,
//      "maxConsumption" -> "-3200",
//      "hysteresisThreshold" -> "300",
//      "prosumerStatus" -> "Flexible",
//      "period" -> "500",
//      "requires.from" -> metaFactory.&("aggregator" -> aggregator))
//      
//  }
//
//  val instances = ListBuffer[ComponentInstance]()
//
//  @Invalidate def stop(): Unit = {
//    instances.foreach(instance => { instance.stop; instance.dispose })
//    instances.clear
//  }
//
//  def spawn(factoryName: String, items: (String, Any)*): Unit = {
//    metaFactory.spawn(factoryName, items: _*).future onSuccess {
//      case componentInstance => instances += componentInstance
//    }
//  }
//
//}