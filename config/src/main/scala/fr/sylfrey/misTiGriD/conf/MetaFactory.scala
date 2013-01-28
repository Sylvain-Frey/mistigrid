package fr.sylfrey.misTiGriD.conf

import java.util.{ List => JList }
import java.util.HashMap
import java.util.Dictionary
import org.apache.felix.ipojo.Factory
import org.osgi.framework.BundleContext
import org.osgi.framework.BundleActivator
import scala.collection.JavaConversions._
import org.osgi.framework.ServiceReference
import org.apache.felix.ipojo.annotations.Component
import org.apache.felix.ipojo.annotations.Bind
import org.apache.felix.ipojo.annotations.Unbind
import org.apache.felix.ipojo.annotations.Provides
import org.apache.felix.ipojo.annotations.Instantiate

trait MetaFactory {
  def spawn(factoryName: String, items: (String, _)*)
  def parse(items: JList[(String, _)]): Dictionary[String, _]
  def &(items: (String, String)*): JList[Tuple2[String, Any]]
}

//@Component
//@Provides(specifications=Array(classOf[MetaFactory]))
//@Instantiate
class MetaFactoryImpl extends MetaFactory {

  val factories = new HashMap[String, Factory]()
  var factRefs: Array[ServiceReference] = _
  val factorables = new HashMap[String, List[Dictionary[String, _]]]

//  @Bind(aggregate=true)
  def bind(factory: Factory) {
    val factoryName = factory.getName()
    factories.put(factoryName, factory)
    // check for pending jobs
    if (factorables.containsKey(factoryName)) {
      factorables.remove(factoryName).foreach(factory.createComponentInstance(_))
    }
  }

//  @Unbind
  def unbind(factory: Factory) {
    factories.remove(factory.getName())
  }

  def spawn(factoryName: String, items: (String, _)*): Unit = {
    if (factories.containsKey(factoryName)) { // factory available: call it
      factories.get(factoryName).createComponentInstance(parse(items))
    } else { // store the job for when factory becomes available
      if (factorables.containsKey(factoryName)) {
        factorables.put(factoryName, factorables.get(factoryName) ++ List(parse(items)))
      } else {
        factorables.put(factoryName, List(parse(items)))
      }
    }
  }

  def parse(items: JList[(String, _)]): Dictionary[String, _] = {
    val map = new HashMap[String, Any]()
    items.foreach(item => {
      item._2 match {
        case config: JList[Tuple2[String, Any]] => map.put(item._1, parse(config))
        case string: String => map.put(item._1, string)
        case erroneous => println("### skipping invalid configuration : " + erroneous)
      }
    })
    DictionaryWrapper(map)
  }

  def &(items: (String, String)*): JList[Tuple2[String, Any]] = {
    items.toList
  }

}