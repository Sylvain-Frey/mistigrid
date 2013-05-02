package fr.sylfrey.misTiGriD.configs.storage

import java.util.{ List => JList, Map => JMap }
import org.apache.felix.ipojo.annotations.Component
import org.apache.felix.ipojo.annotations.Instantiate
import org.apache.felix.ipojo.annotations.Requires
import org.apache.felix.ipojo.annotations.Validate
import scala.concurrent.ExecutionContext
import fr.sylfrey.misTiGriD.deploy.HouseFactory
import fr.sylfrey.misTiGriD.deploy.MetaFactory
import scala.collection.mutable.ListBuffer
import org.apache.felix.ipojo.annotations.Invalidate
import org.apache.felix.ipojo.ComponentInstance

//@Component
//@Instantiate
class SimpleStorage {
  
//  @Requires var houseFactory: HouseFactory = _
//  @Requires var metaFactory: MetaFactory = _
//  implicit val ec = ExecutionContext.Implicits.global
//
//  @Validate def start() = {
//    
//    val storageName = "simpleStorage"
//
//    spawn("SimpleStorage",
//        "MAX_LOAD" -> "-50000",
//        "MAX_POWER_IN" -> "-500",
//        "MAX_POWER_OUT" -> "500",
//        "instance.name" -> storageName,
//        "prosumedPower" -> "0",
//        "period" -> "500")
//        
//    spawn("StorageLayout",
//        "instance.name" -> (storageName + "_layout"),
//        "layout.name" -> (storageName + "_layout"),
//        "x" -> "200",
//        "y" -> "30",
//        "width" -> "100",
//        "height" -> "80",
//        "layer" -> "10",
//        "requires.from" -> &("storage" -> storageName))
//
//    spawn("SimpleStorageManager",
//        "instance.name" -> "simpleStorageManager",
//        "houseLoadManagerURI" -> "akka://MisTiGriD/user/houseLoadManager",
//        "actorPath" -> "simpleStorageManager",
//        "period" -> "500",
//        "requires.from" -> &("storage" -> storageName))
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