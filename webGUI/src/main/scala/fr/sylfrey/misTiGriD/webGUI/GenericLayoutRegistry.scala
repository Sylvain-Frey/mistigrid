package fr.sylfrey.misTiGriD.webGUI

import org.osgi.service.http.HttpService
import org.apache.felix.ipojo.annotations.Requires
import fr.sylfrey.misTiGriD.layout.Layout
import org.apache.felix.ipojo.annotations.Bind
import fr.sylfrey.misTiGriD.layout.HeaterManagerLayout
import org.codehaus.jackson.map.ObjectMapper
import fr.sylfrey.misTiGriD.layout.OpeningLayout
import fr.sylfrey.misTiGriD.layout.HeaterLayout
import fr.sylfrey.misTiGriD.layout.LampLayout
import fr.sylfrey.misTiGriD.layout.ProsumerLayout
import fr.sylfrey.misTiGriD.layout.ThermicObjectLayout
import fr.sylfrey.misTiGriD.layout.AtmosphereLayout
import java.util.HashMap
import org.apache.felix.ipojo.annotations.Unbind
import scala.collection.JavaConversions._
import org.osgi.service.http.NamespaceException
import javax.servlet.ServletException
import org.apache.felix.ipojo.annotations.Validate
import org.apache.felix.ipojo.annotations.Invalidate
import org.apache.felix.ipojo.annotations.Component
import fr.sylfrey.misTiGriD.layout.LampManagerLayout
import fr.sylfrey.misTiGriD.layout.LoadManagerLayout

@Component(name="LayoutRegistry",immediate=true)
class GenericLayoutRegistry {

  @Requires var httpService: HttpService = _

  val layouts = new HashMap[Tuple2[Class[_ <: Layout], String], Layout]()
  
  val INDEX_PATH = "/layoutsIndex"
  val LAYOUT_PREFIX = "/layouts"
  val WEBGUI_PREFIX = "/webgui"

  var hmlCounter = 0
  var olCounter = 0

  @Bind(specification = "fr.sylfrey.misTiGriD.layout.Layout", aggregate = true, optional = true)
  def bind(layout: Layout): Unit = layout match {
    case l: AtmosphereLayout => store[AtmosphereLayout](classOf[AtmosphereLayout], l.getName, l)
    case l: HeaterLayout => store[HeaterLayout](classOf[HeaterLayout], l.getName, l)
    case l: LampLayout => store[LampLayout](classOf[LampLayout], l.getName, l)
    case l: LampManagerLayout => store[LampManagerLayout](classOf[LampManagerLayout], l.name, l)
    case l: ThermicObjectLayout => store[ThermicObjectLayout](classOf[ThermicObjectLayout], l.getName, l)
    case l: ProsumerLayout => store[ProsumerLayout](classOf[ProsumerLayout], l.getName, l)
    case l: HeaterManagerLayout => {
      hmlCounter += 1
      store[HeaterManagerLayout](classOf[HeaterManagerLayout], Serialiser.HeaterManagerLayout + hmlCounter, l)
    }
    case l: OpeningLayout => {
      olCounter += 1
      store[OpeningLayout](classOf[OpeningLayout], Serialiser.OpeningLayout + olCounter, l)      
    }
    case l: LoadManagerLayout => store[LoadManagerLayout](classOf[LoadManagerLayout], l.name, l)
    case l => println("### warning: bad Layout " + l.getClass() + " not bound to GenericLayoutRegistry")
  }
  
  @Unbind(specification="fr.sylfrey.misTiGriD.layout.Layout",aggregate=true)
  def unbind(layout : Layout) : Unit = {
    for (entry <- layouts.entrySet()) {
      if (entry.getValue == layout) layouts.remove(entry.getKey)
    }
  }
  
  def store[L <: Layout](clazz: Class[_ <: L], name: String, layout: L) {
    layouts.put((clazz, name), layout)
  }
  
  @Validate def start() : Unit = {
    try {
	  httpService.registerResources(WEBGUI_PREFIX, "/WebGUI", null); 
	} catch {
	  case e : NamespaceException => println("# web resources already registered")
	}

	try {
	  httpService.registerServlet(INDEX_PATH, new IndexServlet(this), null, null); 
	  httpService.registerServlet(LAYOUT_PREFIX, new TouchpointServlet(this), null, null);
	} catch {
	  case e : NamespaceException => println("# web resource already registered")
	  case e : Throwable => e.printStackTrace();
	}
  }

  @Invalidate def stop() : Unit = {
	httpService.unregister(WEBGUI_PREFIX);
	httpService.unregister(INDEX_PATH);
	httpService.unregister(LAYOUT_PREFIX);
  }
  
  
  def get[L <: Layout](clazz: Class[L], name: String) = {
    layouts.get((clazz, name)).asInstanceOf[L]
  }

}