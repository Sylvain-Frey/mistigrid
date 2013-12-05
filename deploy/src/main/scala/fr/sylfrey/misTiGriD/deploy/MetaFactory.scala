/*******************************************************************************
 * Copyright (c) 2013 EDF. This software was developed with the 
 * collaboration of Télécom ParisTech (Sylvain Frey).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Sylvain Frey - initial API and implementation
 ******************************************************************************/
package fr.sylfrey.misTiGriD.deploy

import java.util.{ List => JList, Map => JMap }
import java.util.HashMap
import java.util.Dictionary

import scala.concurrent.{promise, Promise, Future, ExecutionContext}
import scala.collection.JavaConversions._
import scala.language.existentials

import org.osgi.framework.ServiceReference
import org.apache.felix.ipojo.annotations.Component
import org.apache.felix.ipojo.annotations.Bind
import org.apache.felix.ipojo.annotations.Unbind
import org.apache.felix.ipojo.annotations.Provides
import org.apache.felix.ipojo.annotations.Instantiate
import org.apache.felix.ipojo.Factory
import org.apache.felix.ipojo.ComponentInstance

/**
 * MetaFactory aggregates all available iPOJO factories
 * and allows to spawn (create instances of) iPOJO components.
 * Cf. fr.sylfrey.misTiGriD.configs.* and
 * fr.sylfrey.misTiGriD.deploy.HouseFactory for example use.
 */
trait MetaFactory {
  
  /**
   * Available iPOJO factories. 
   */
  def factories: JMap[String, Factory]
  
  /**
   * Create an iPOJO component instance 
   * via factory with name factoryName
   * and with configuration config.
   */
  def spawn(factoryName: String, config: (String, _)*): Promise[ComponentInstance]
  
  /**
   * Parse a list of (key, value) tuples 
   * and generate an iPOJO-compatible instance configuration.
   */
  def parse(items: JList[(String, _)]): Dictionary[String, _]
  
  /**
   * Utility function for generating configurations.
   */
  def &(items: (String, String)*): JList[(String, Any)]
  
} 


@Component
@Provides(specifications=Array(classOf[MetaFactory]))
@Instantiate
class MetaFactoryImpl extends MetaFactory {
  
  // implicit execution context required for Promise completion
  implicit val ec = ExecutionContext.Implicits.global

  // list of available factories
  val factories = new HashMap[String, Factory]
  
  // list of pending instantiation jobs 
  // waiting for an appropriate factory to become available
  val factorables = new HashMap[
                                String, 
                                List[
                                  (Promise[ComponentInstance], Dictionary[String, _])
                                ]
                         ]
  
  
  @Bind(aggregate=true)
  def bind(factory: Factory) {
    
    val factoryName = factory.getName()
    factories.put(factoryName, factory)
    
    // check for pending jobs
    if (factorables.containsKey(factoryName)) {
      // there are pending jobs for the new bound factory: execute them
      factorables.remove(factoryName).foreach( _ match {
    	case (promise, config) => 
    	  promise success factory.createComponentInstance(config)
      })
    }
  }

  @Unbind
  def unbind(factory: Factory) {
    factories.remove(factory.getName())
  }
  
  def spawn(factoryName: String, items: (String, _)*) = {
    
    val p = promise[ComponentInstance]
    val config = parse(items)
    
    if (factories.containsKey(factoryName)) { // factory available: call it
    
      p success factories.get(factoryName).createComponentInstance(config)
      
    } else { // store the job for when factory becomes available
      
      val job = (p, config)
      if (factorables.containsKey(factoryName)) {
        factorables.put(factoryName, job +: factorables.get(factoryName))
      } else {
        factorables.put(factoryName, List(job) )
      }
      
    }
    
    p
    
  }

  def parse(items: JList[(String, _)]) = {
    val map = new HashMap[String, Any]
    items.foreach { case (key, value) => value match {
        case config: JList[(String, Any) @ unchecked] => map.put(key, parse(config))
        case string: String => map.put(key, string)
        case erroneous => println("### Error: MetaFactory skipping invalid configuration : " + (key, value))
      }
    }
    asJavaDictionary(map)
  }

  def &(items: (String, String)*) = {
    items.toList
  }

}
