package fr.sylfrey.misTiGriD.webGUI

import java.util.HashMap
import scala.collection.JavaConversions.asScalaSet
import org.apache.felix.ipojo.annotations.Bind
import org.apache.felix.ipojo.annotations.Component
import org.apache.felix.ipojo.annotations.Invalidate
import org.apache.felix.ipojo.annotations.Requires
import org.apache.felix.ipojo.annotations.Unbind
import org.apache.felix.ipojo.annotations.Validate
import org.osgi.service.http.HttpService
import org.osgi.service.http.NamespaceException
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
import fr.sylfrey.misTiGriD.alba.basic.model.Schedule

@Component(name="LayoutRegistry",immediate=true)
class GenericLayoutRegistry {

  @Requires var httpService: HttpService = _
  @Requires var schedule: Schedule = _

  val layouts = new HashMap[Tuple2[Class[_ <: Layout], String], Layout]()
  
  val INDEX_PATH = "/layoutsIndex"
  val LAYOUT_PREFIX = "/layouts"
  val WEBGUI_PREFIX = "/webgui"
  val SCHEDULE_PREFIX = "/schedule"

  var hmlCounter = 0
  var olCounter = 0

  @Bind(specification = "fr.sylfrey.misTiGriD.layout.Layout", aggregate = true, optional = true)
  def bind(layout: Layout): Unit = layout match {
    case l: AtmosphereLayout => store[AtmosphereLayout](classOf[AtmosphereLayout], l.getName, l)
    case l: StorageLayout => store[StorageLayout](classOf[StorageLayout], l.getName, l)
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
	  httpService.registerServlet(SCHEDULE_PREFIX, new ScheduleServlet(schedule), null, null);
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