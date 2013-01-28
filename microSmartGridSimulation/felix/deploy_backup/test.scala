

import fr.tpt.s3.microSmartGridSimulation.temperature.ThermicObject
import org.apache.felix.ipojo.architecture.Architecture
import org.apache.felix.ipojo.Pojo
import org.apache.felix.ipojo.Factory

val newHeater = "newHeater"

$[HouseFactory].makeHeater(newHeater, Heater(0, 0.05f, 0.1f, 400f, "houseAggregator", House.kitchen))

$[HouseFactory].makeHeaterManager(newHeater  + "_manager", HeaterManager(newHeater  + "_manager", 50, 22, true, "loadTopic", "aggregatorController", "loadHierarch", newHeater,  House.kitchen))

$[HouseFactory].makeHeaterLayout(newHeater + "_layout", Dim(100,110,90,50,10), newHeater)

$[HouseFactory].makeHeaterManagerLayout(newHeater + "_manager_layout", Dim(100,60,90,50,10), newHeater + "_manager")



val kitchenRef = bundleContext.getServiceReferences(classOf[ThermicObject], "(|(instance.name=kitchen)(service.pid=kitchen))").iterator.next

val kitchenPOJO = bundleContext.getService(kitchenRef).asInstanceOf[fr.tpt.s3.microSmartGridSimulation.temperature.impl.ThermicObjectImpl]

val newHeaterRef = bundleContext.getServiceReferences(classOf[fr.tpt.s3.microSmartGridSimulation.appliances.Heater], "(|(instance.name=newHeater)(service.pid=newHeater))").iterator.next

val newHeater = bundleContext.getService(newHeaterRef).asInstanceOf[fr.tpt.s3.microSmartGridSimulation.appliances.Heater]






val kitchenM = kitchenPOJO.asInstanceOf[Pojo].getComponentInstance



var props = new java.util.Properties

props.put("instance.name","kitchenName")

kitchenM.reconfigure(props)






val kitchenFactory = bundleContext.getServiceReferences(classOf[Factory], "(|(instance.name=ThermicObject)(service.pid=ThermicObject))").iterator.next

val archRefs = bundleContext.getServiceReferences(classOf[Architecture], "(instance.name=*)")

val archServices = for (ref <- archRefs) yield bundleContext.getService(ref)

val kitchenArch = archServices.filter(a => a.getInstanceDescription.getName == "kitchen").iterator.next

val kitchenDescption = kitchenArch.getInstanceDescription

archServices.foreach(s => println(s.getInstanceDescription.getDescription))
