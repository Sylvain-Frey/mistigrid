package fr.sylfrey.misTiGriD.webGUI

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest
import java.lang.Float
import java.lang.Boolean
import fr.sylfrey.misTiGriD.layout.HeaterManagerLayout
import fr.sylfrey.misTiGriD.layout.HeaterLayout
import fr.sylfrey.misTiGriD.layout.OpeningLayout
import fr.sylfrey.misTiGriD.layout.LampLayout
import fr.sylfrey.misTiGriD.layout.Layout
import fr.sylfrey.misTiGriD.layout.AtmosphereLayout
import fr.sylfrey.misTiGriD.layout.ProsumerLayout
import fr.sylfrey.misTiGriD.layout.ThermicObjectLayout
import org.codehaus.jackson.map.ObjectMapper
import fr.sylfrey.misTiGriD.management.resources.loadHierarch.LoadHierarch
import fr.sylfrey.misTiGriD.layout.LampManagerLayout

class TouchpointServlet(registry : GenericLayoutRegistry) extends HttpServlet {
  
  val mapper = new ObjectMapper()

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    
    val path = req.getPathInfo().split("/")
    var layoutType : String = ""
    var layoutName : String = ""

    if (path.length < 3) {
      if(!path(1).equals(Serialiser.AllLayouts)){
		resp.getWriter().write("Error 404: " + req.getPathInfo() + " not found.")
		return			
	  }
	  layoutType = path(1)
    } else {
      layoutType = path(1)
      layoutName = path(2)
    }

    val node = mapper.createObjectNode()
    var response = "null"

    if (layoutType.equals(Serialiser.AtmosphereLayout)) {
      val atmosphereLayout = registry.get[AtmosphereLayout](classOf[AtmosphereLayout], layoutName)
      if (atmosphereLayout != null) response = Serialiser.serialise(atmosphereLayout, node).toString()
				
    } else if (layoutType.equals(Serialiser.ThermicObjectLayout)) {
      val layout = registry.get[ThermicObjectLayout](classOf[ThermicObjectLayout], layoutName)
      if (layout!= null) response = Serialiser.serialise(layout, node).toString() 

    } else if (layoutType.equals(Serialiser.HeaterLayout)) {
      val layout = registry.get[HeaterLayout](classOf[HeaterLayout], layoutName)
      if (layout!= null) response = Serialiser.serialise(layout, node).toString() 

    } else if (layoutType.equals(Serialiser.ProsumerLayout)) {
      val layout = registry.get[ProsumerLayout](classOf[ProsumerLayout], layoutName)
      if (layout!= null) response = Serialiser.serialise(layout, node).toString() 

    } else if (layoutType.equals(Serialiser.HeaterManagerLayout)) {
      val layout = registry.get[HeaterManagerLayout](classOf[HeaterManagerLayout], layoutName)
      if (layout!= null) response = Serialiser.serialise(layout, node).toString() 

    } else if (layoutType.equals(Serialiser.LampManagerLayout)) {
      val layout = registry.get[LampManagerLayout](classOf[LampManagerLayout], layoutName)
      if (layout!= null) response = Serialiser.serialise(layout, node).toString() 

    } else if (layoutType.equals(Serialiser.OpeningLayout)) {
      val layout = registry.get[OpeningLayout](classOf[OpeningLayout], layoutName)
      if (layout!= null) response = Serialiser.serialise(layout, node).toString() 

    } else if (layoutType.equals(Serialiser.LampLayout)) {
      val layout = registry.get[LampLayout](classOf[LampLayout], layoutName)
      if (layout!= null) response = Serialiser.serialise(layout, node).toString() 

    } else if (layoutType.equals(Serialiser.AllLayouts)){
      response = Serialiser.serialiseUpdate(node, registry.layouts).toString()
				
    } 
//			else if (layoutType.equals(Serialiser.LoadHierarch)){
//				if (houseLoadManager!= null) response = Serialiser.serialise(houseLoadManager, node).toString()
//			}

    resp.getWriter().write(response)

  }	

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val path = req.getPathInfo().split("/")
    if (path.length < 3) {

      resp.getWriter().write("### error, invalid path: " + req.getPathInfo())
      return

    }

    val layoutType = path(1)
    val layoutName = path(2)
    val data = req.getParameter("info")

    if (data != null) {

      if (layoutType.equals(Serialiser.HeaterManagerLayout)) {

        val heaterMan = registry.get[HeaterManagerLayout](classOf[HeaterManagerLayout], layoutName)
        val temperature = Float.parseFloat(data)
        heaterMan.setRequiredTemperature(temperature)

      } else if (layoutType.equals(Serialiser.HeaterLayout)) {

        val heaterMan = registry.get[HeaterLayout](classOf[HeaterLayout], layoutName)
        val power = Float.parseFloat(data)
        heaterMan.setEmissionPower(power)

      } else if (layoutType.equals(Serialiser.OpeningLayout)) {

        val wallLayoutObj = registry.get[OpeningLayout](classOf[OpeningLayout], layoutName)
        val status = Boolean.parseBoolean(data)
        if (status) {
          wallLayoutObj.open()
        } else {
          wallLayoutObj.close()
        }

      } else if (layoutType.equals(Serialiser.LampLayout)) {

        val lamp = registry.get[LampLayout](classOf[LampLayout], layoutName)
        val power = Boolean.parseBoolean(data)
        if (power) { // turn on
          lamp.setEmissionPower(1023f / 17.05f)
          lamp.turnOn() // wtf? intern!
        } else { // turn off
          lamp.setEmissionPower(0)
          lamp.turnOff() // again
        }

      } else if (layoutType.equals(Serialiser.AtmosphereLayout)) {

        val increase = Boolean.parseBoolean(data)
        val atmosphereLayout = registry.get[AtmosphereLayout](classOf[AtmosphereLayout], layoutName)
        if (increase) { // raise temperature
          atmosphereLayout.setBaseTemperature(atmosphereLayout.getBaseTemperature() + 0.5f)
        } else { // decrease temperature
          atmosphereLayout.setBaseTemperature(atmosphereLayout.getBaseTemperature() - 0.5f)
        }

      }

    }
  }

}