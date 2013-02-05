package fr.sylfrey.misTiGriD.deploy

import java.util.{ List => JList, Map => JMap }
import java.util.HashMap
import java.util.Dictionary
import scala.collection.JavaConversions._
import org.osgi.framework.ServiceReference
import org.apache.felix.ipojo.annotations.Component
import org.apache.felix.ipojo.annotations.Bind
import org.apache.felix.ipojo.annotations.Unbind
import org.apache.felix.ipojo.annotations.Provides
import org.apache.felix.ipojo.annotations.Instantiate
import org.apache.felix.ipojo.Factory
import scala.concurrent.{promise, Promise, Future, ExecutionContext}
import org.apache.felix.ipojo.ComponentInstance

trait MetaFactory {
  def factories: JMap[String, Factory]
  def spawn(factoryName: String, items: (String, _)*): Promise[ComponentInstance]
  def parse(items: JList[(String, _)]): Dictionary[String, _]
  def &(items: (String, String)*): JList[Tuple2[String, Any]]
} 

@Component
@Provides(specifications=Array(classOf[MetaFactory]))
@Instantiate
class MetaFactoryImpl extends MetaFactory {
  
  implicit val ec = ExecutionContext.Implicits.global

  val _factories = new HashMap[String, Factory]()
  
  val factorables = new HashMap[
                                String, 
                                List[
                                  Tuple2[Promise[ComponentInstance], Dictionary[String, _]]
                                ]
                         ]
  
  
  @Bind(aggregate=true)
  def bind(factory: Factory) {
    val factoryName = factory.getName()
    _factories.put(factoryName, factory)
    // check for pending jobs
    if (factorables.containsKey(factoryName)) {
    	factorables.remove(factoryName).foreach( _ match {
    	  case (promise, config) => promise success factory.createComponentInstance(config)
    	})
    }
  }

  @Unbind
  def unbind(factory: Factory) {
    _factories.remove(factory.getName())
  }
  
  def factories: JMap[String, Factory] = _factories

  def spawn(factoryName: String, items: (String, _)*) : Promise[ComponentInstance] = {
    val p = promise[ComponentInstance]
    if (_factories.containsKey(factoryName)) { // factory available: call it
      p success _factories.get(factoryName).createComponentInstance(parse(items))
    } else { // store the job for when factory becomes available
      val job = Tuple2(p, parse(items))
      if (factorables.containsKey(factoryName)) {
        factorables.put(factoryName, job +: factorables.get(factoryName))
      } else {
        factorables.put(factoryName, List(job) )
      }
    }
    p
  }

  def parse(items: JList[(String, _)]): Dictionary[String, _] = {
    val map = new HashMap[String, Any]()
    items.foreach { case (key, value) => value match {
        case config: JList[Tuple2[String, Any]] => map.put(key, parse(config))
        case string: String => map.put(key, string)
        case erroneous => println("### skipping invalid configuration : " + (key, value))
      }
    }
    DictionaryWrapper(map)
  }

  def &(items: (String, String)*): JList[Tuple2[String, Any]] = {
    items.toList
  }

}