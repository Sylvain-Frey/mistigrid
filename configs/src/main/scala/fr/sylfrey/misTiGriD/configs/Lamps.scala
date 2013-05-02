package fr.sylfrey.misTiGriD.configs.lamps

import java.util.{ List => JList, Map => JMap }

import org.apache.felix.ipojo.annotations.Component
import org.apache.felix.ipojo.annotations.Instantiate
import org.apache.felix.ipojo.annotations.Requires
import org.apache.felix.ipojo.annotations.Validate
import org.apache.felix.ipojo.annotations.Invalidate
import org.apache.felix.ipojo.ComponentInstance

import scala.concurrent.ExecutionContext
import scala.collection.mutable.ListBuffer

import fr.sylfrey.misTiGriD.deploy.HouseFactory
import fr.sylfrey.misTiGriD.deploy.MetaFactory

//@Component
//@Instantiate
class Lamps {

//  @Requires var houseFactory: HouseFactory = _
//  @Requires var metaFactory: MetaFactory = _
//  implicit val ec = ExecutionContext.Implicits.global
//
//  @Validate def start() = {
//
//    val lamps = Map(
//      "lamp_1" -> (240, 230),
//      "lamp_2" -> (580, 330),
//      "lamp_3" -> (720, 150),
//      "lamp_4" -> (360, 760),
//      "lamp_5" -> (610, 760)
//    )
//
//    lamps.foreach { case (name : String,(x : Int, y : Int)) =>
//      spawn("Lamp",
//        "instance.name" -> name,
//        "prosumedPower" -> "0",
//        "maxEmissionPower" -> "100",
//        "requires.from" -> &("aggregator" -> "houseAggregator"))
//
//      spawn("LampLayout",
//        "instance.name" -> (name + "_layout"),
//        "layout.name" -> (name + "_layout"),
//        "x" -> x.toString,
//        "y" -> y.toString,
//        "width" -> "100",
//        "height" -> "120",
//        "layer" -> "10",
//        "requires.from" -> &("lamp" -> name))
//        
//      spawn("LampManager",
//        "instance.name" -> (name + "_manager"),          
//        "ecoMaxPower" -> "-30",
//        "prosumerStatus" -> "Flexible",
//        "period" -> "500",
//        "actorPath" -> (name + "_manager"),
//        "houseLoadManagerURI" -> "akka://MisTiGriD/user/houseLoadManager",
//        "requires.from" -> &("lamp" -> name))
//      
//      spawn("LampManagerLayout",
//        "instance.name" -> (name + "_manager_layout"),
//        "layout.name" -> (name + "_mgr"),
//        "x" -> (x-5).toString,
//        "y" -> (y-60).toString,
//        "width" -> "115",
//        "height" -> "80",
//        "layer" -> "10",          
//        "requires.from" -> &("manager" -> (name + "_manager")))
//       
//    }
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
//  def &(items: (String, String)*): JList[Tuple2[String, Any]] = {
//    metaFactory.&(items: _*)
//  }

}
