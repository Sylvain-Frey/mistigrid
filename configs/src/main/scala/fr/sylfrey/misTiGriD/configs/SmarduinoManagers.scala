package fr.sylfrey.misTiGriD.configs.smarduinoManagers

import java.util.{ List => JList, Map => JMap }
import fr.sylfrey.misTiGriD.deploy.HouseFactory
import scala.concurrent.ExecutionContext
import fr.sylfrey.misTiGriD.deploy.MetaFactory
import org.apache.felix.ipojo.annotations.Instantiate
import org.apache.felix.ipojo.annotations.Requires
import org.apache.felix.ipojo.annotations.Validate
import org.apache.felix.ipojo.annotations.Component
import fr.sylfrey.misTiGriD.deploy.HeaterManager
import org.apache.felix.ipojo.annotations.Invalidate
import scala.collection.mutable.ListBuffer
import org.apache.felix.ipojo.ComponentInstance
import akka.dispatch.Foreach
import fr.sylfrey.misTiGriD.deploy.Dim

//@Component
//@Instantiate
class SmarduinoManagers {

//  @Requires var houseFactory: HouseFactory = null
//  @Requires var metaFactory: MetaFactory = null
//  implicit val ec = ExecutionContext.Implicits.global
//
//  @Validate def start() = {
//
//    val aggregator = "houseAggregator"
//    val houseLoadManager = "houseLoadManager"
//    val loadManagerURI = "akka://MisTiGriD/user/" + houseLoadManager
//    val districtLoadManagerURI = "akka://MisTiGriD@localhost:4004/user/districtLoadManager"
//
//    spawn("HouseLoadManagerDeployer",
//      "instance.name" -> houseLoadManager,
//      "actorPath" -> houseLoadManager,
//      "maxConsumption" -> "-1600",
//      "hysteresisThreshold" -> "300",
//      "prosumerStatus" -> "Flexible",
//      "period" -> "500",
//      "hasParent" -> "false",
//      "districtLoadManagerURI" -> districtLoadManagerURI,
//      "requires.from" -> metaFactory.&("aggregator" -> aggregator))
//
//    Array(
//        
//        ("heater_kitchen",		"kitchen",		60,		630),
//        ("heater_bedroom1",		"bedroom1",		860,	590),
//        ("heater_bedroom2",		"bedroom2",		660,	630),
//        ("heater_bedroom3",		"bedroom3",		340,	630),
//        ("heater_bathroom",		"bathroom",		860,	270),
//        ("heater_entrance",		"entrance",		680,	170),
//        ("heater_livingRoom1",	"livingRoom1",	60,		170),
//        ("heater_livingRoom2",	"livingRoom2",	460,	310)
//        
//    ).foreach { case (heater, room, x, y) =>
//      
//      val manager = heater + "_manager"
//      houseFactory.makeHeaterManager(manager, HeaterManager(
//        actorPath = manager,
//        period = 500,
//        requiredTemperature = 22,
//        prosumerStatus = "Flexible",
//        houseLoadManagerURI = loadManagerURI,
//        heater = heater,
//        room = room,
//        kp = 3, ki = 0, kd = 0.5f))
//        
//      houseFactory.makeHeaterManagerLayout(heater + "_layout", 
//          Dim(x ,y, 110, 50, 10), 
//          manager)
//      
//    }
//    
//    Array(
//        
//        ("lamp_livingRoom1",	60,		370),
//        ("lamp_livingRoom2",	170,	470),
//        ("lamp_bedroom1",		1080,	600),
//        ("lamp_bedroom2",		750,	530),
//        ("lamp_bedroom3",		465,	670),
//        ("lamp_entrance",		650,	300),
//        ("lamp_wc",				570,	480),
//        ("lamp_bathroom",		1080,	300),
//        ("lamp_kitchen",		185,	670)
//        
//    ).foreach { case (lamp, x, y) =>
//      
//      val manager = lamp + "_manager"
//      spawn("LampManager",
//        "instance.name" -> manager,          
//        "ecoMaxPower" -> "-30",
//        "prosumerStatus" -> "Flexible",
//        "period" -> "500",
//        "actorPath" -> manager,
//        "houseLoadManagerURI" -> loadManagerURI,
//        "requires.from" -> &("lamp" -> lamp))
//      
//      spawn("LampManagerLayout",
//        "instance.name" -> (manager + "_layout"),
//        "layout.name" -> manager,
//        "x" -> x.toString,
//        "y" -> (y-30).toString,
//        "width" -> "50",
//        "height" -> "30",
//        "layer" -> "10",          
//        "requires.from" -> &("manager" -> manager))
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