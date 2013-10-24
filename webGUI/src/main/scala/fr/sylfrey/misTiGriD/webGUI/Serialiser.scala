package fr.sylfrey.misTiGriD.webGUI

import java.util.Map
import scala.collection.JavaConversions.asScalaSet
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.node.ObjectNode
import fr.sylfrey.misTiGriD.alba.basic.messages.ProsumerStatus
import fr.sylfrey.misTiGriD.layout.AtmosphereLayout
import fr.sylfrey.misTiGriD.layout.HeaterLayout
import fr.sylfrey.misTiGriD.layout.HeaterManagerLayout
import fr.sylfrey.misTiGriD.layout.LampLayout
import fr.sylfrey.misTiGriD.layout.LampManagerLayout
import fr.sylfrey.misTiGriD.layout.Layout
import fr.sylfrey.misTiGriD.layout.LoadManagerLayout
import fr.sylfrey.misTiGriD.layout.OpeningLayout
import fr.sylfrey.misTiGriD.layout.ProsumerLayout
import fr.sylfrey.misTiGriD.layout.ThermicObjectLayout
import fr.sylfrey.misTiGriD.layout.StorageLayout

object Serialiser {

  val mapper = new ObjectMapper()

  val AtmosphereLayout = "AtmosphereLayout"
  val ThermicObjectLayout = "ThermicObjectLayout"
  val ProsumerLayout = "ProsumerLayout"
  val StorageLayout = "StorageLayout"
  val HeaterLayout = "HeaterLayout"
  val HeaterManagerLayout = "HeaterManagerLayout"
  val OpeningLayout = "OpeningLayout"
  val LampLayout = "LampLayout"
  val LampManagerLayout = "LampManagerLayout"
  val LoadManagerLayout = "LoadManagerLayout"
  val AllLayouts = "AllLayouts"

  def serialise(layout: Layout, node: ObjectNode): ObjectNode = {
    node.put("x", layout.x())
    node.put("y", layout.y())
    node.put("width", layout.width())
    node.put("height", layout.height())
    node.put("layer", layout.layer())
    node
  }

  def serialise(layout: OpeningLayout, node: ObjectNode): ObjectNode = {
    serialise(layout.asInstanceOf[Layout], node)
    node.put("type", OpeningLayout)
    node.put("height", layout.height())
    node.put("width", layout.width())
    node.put("isOpen", layout.isOpen())
    node.put("isClosed", layout.isClosed())
    node
  }
  
  def serialise(layout: LampManagerLayout, node: ObjectNode): ObjectNode = {
    serialise(layout.asInstanceOf[Layout], node)
    node.put("type", LampManagerLayout)
    node.put("isEconomising", layout.isEconomising)
    node.put("status", ProsumerStatus.toString(layout.getStatus))
    node
  }
  
  def serialise(layout: HeaterManagerLayout, node: ObjectNode): ObjectNode = {
    serialise(layout.asInstanceOf[Layout], node)
    node.put("type", HeaterManagerLayout)
    node.put("name", layout.name())
    node.put("requiredTemperature", layout.getRequiredTemperature())
    node.put("isEconomizing", layout.isEconomizing())
    node.put("status", ProsumerStatus.toString(layout.getStatus))
    node
  }

  def serialise(layout: AtmosphereLayout, node: ObjectNode): ObjectNode = {
    serialise(layout.asInstanceOf[ThermicObjectLayout], node)
    node.put("type", AtmosphereLayout)
    node.put("name", layout.getName())
    node.put("currentTemperature", layout.getCurrentTemperature())
    node
  }

  def serialise(layout: ThermicObjectLayout, node: ObjectNode): ObjectNode = {
    serialise(layout.asInstanceOf[Layout], node)
    node.put("type", ThermicObjectLayout)
    node.put("name", layout.getName())
    node.put("currentTemperature", layout.getCurrentTemperature())
    node
  }

  def serialise(layout: ProsumerLayout, node: ObjectNode): ObjectNode = {
    serialise(layout.asInstanceOf[Layout], node)
    node.put("type", ProsumerLayout)
    node.put("name", layout.getName())
    node.put("prosumedPower", layout.getProsumedPower())
    node
  }
  
  def serialise(layout: StorageLayout, node: ObjectNode): ObjectNode = {
    serialise(layout.asInstanceOf[Layout], node)
    node.put("type", StorageLayout)
    node.put("name", layout.getName())
    node.put("load", layout.getLoad())
    node.put("loadCapacity", layout.getLoadCapacity())
    node.put("state", layout.getState().toString())
    node.put("prosumedPower", layout.getProsumedPower())
    node
  }
  
  def serialise(layout: HeaterLayout, node: ObjectNode): ObjectNode = {
    serialise(layout.asInstanceOf[Layout], node)
    node.put("type", HeaterLayout)
    node.put("emissionPower", layout.getEmissionPower())
    node.put("maxEmissionPower", layout.getMaxEmissionPower())
    node
  }

  def serialise(layout: LampLayout, node: ObjectNode): ObjectNode = {
    serialise(layout.asInstanceOf[Layout], node)
    node.put("type", LampLayout)
    node.put("name", layout.getName())
    node.put("prosumedPower", layout.getProsumedPower())
    node.put("emissionPower", layout.getEmissionPower())
    node.put("maxEmissionPower", layout.getMaxEmissionPower())
    node
  }

  def serialise(layout: LoadManagerLayout, node: ObjectNode): ObjectNode = {
    serialise(layout.asInstanceOf[Layout], node)
    node.put("type", LoadManagerLayout)
    node.put("prosumption", layout.getProsumption().prosumption)
    node.put("maxConsumptionThreshold", layout.maxConsumptionThreshold())
    node.put("status", ProsumerStatus.toString(layout.getStatus))
    node
  }

  def store(node: ObjectNode, name: String, layout: Layout) {
    val obj = mapper.createObjectNode()
    layout match {
      case layout: HeaterLayout => serialise(layout, obj);			
      case layout: StorageLayout => serialise(layout, obj);		
      case layout: LampLayout => serialise(layout, obj);			
      case layout: LampManagerLayout => serialise(layout, obj);		
      case layout: HeaterManagerLayout => serialise(layout, obj);	
      case layout: OpeningLayout => serialise(layout, obj);			
      case layout: AtmosphereLayout => serialise(layout, obj);		
      case layout: ProsumerLayout => serialise(layout, obj);		
      case layout: ThermicObjectLayout => serialise(layout, obj);	
      case layout: LoadManagerLayout => serialise(layout, obj);		
    }    
    node.put(name, obj)
  }

  def serialiseUpdate(node: ObjectNode, layouts: Map[Tuple2[Class[_ <: Layout], String], Layout]): ObjectNode = {

    for (entry <- layouts.entrySet) store(node, entry.getKey._2, entry.getValue())
    node

  }

  def index(layouts: Map[Tuple2[Class[_ <: Layout], String], Layout]): String = {
    val index = mapper.createObjectNode()

    val atmAN = mapper.createArrayNode()
    val tols = mapper.createArrayNode()
    val pls = mapper.createArrayNode()
    val hls = mapper.createArrayNode()
    val sls = mapper.createArrayNode()
    val hmls = mapper.createArrayNode()
    val ols = mapper.createArrayNode()
    val lls = mapper.createArrayNode()
    val lmls = mapper.createArrayNode()
    val ldmls = mapper.createArrayNode()


    for (entry <- layouts.entrySet) {
      val name = entry.getKey()._2
      entry.getValue match {
        case layout: HeaterLayout => hls.add(name)
        case layout: StorageLayout => sls.add(name)
        case layout: LampLayout => lls.add(name)
        case layout: LampManagerLayout => lmls.add(name)
        case layout: LoadManagerLayout => ldmls.add(name)
        case layout: HeaterManagerLayout => hmls.add(name)
        case layout: OpeningLayout => ols.add(name)
        case layout: AtmosphereLayout => atmAN.add(name)
        case layout: ProsumerLayout => pls.add(name)
        case layout: ThermicObjectLayout => tols.add(name)
      }
    }

    index.put(AtmosphereLayout, atmAN)
    index.put(ThermicObjectLayout, tols)
    index.put(ProsumerLayout, pls)
    index.put(StorageLayout, sls)
    index.put(LampLayout, lls)
    index.put(LampManagerLayout, lmls)
    index.put(LoadManagerLayout, ldmls)
    index.put(HeaterLayout, hls)
    index.put(HeaterManagerLayout, hmls)
    index.put(OpeningLayout, ols)
    
    index.toString
  }

}