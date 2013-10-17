package fr.sylfrey.misTiGriD.webGUI

import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConversions.mapAsScalaConcurrentMap
import org.apache.felix.ipojo.annotations.Component
import org.apache.felix.ipojo.annotations.Instantiate
import org.apache.felix.ipojo.annotations.Invalidate
import org.apache.felix.ipojo.annotations.Provides
import org.apache.felix.ipojo.annotations.Requires
import org.apache.felix.ipojo.annotations.Validate
import org.codehaus.jackson.map.ObjectMapper
import org.osgi.service.http.HttpService
import akka.actor.Actor
import akka.actor.Props
import fr.sylfrey.akka.ActorSystemProvider
import fr.sylfrey.misTiGriD.trace.Archiver
import fr.sylfrey.misTiGriD.trace.ArchiverEvent
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import akka.event.EventBus
import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.event.EventBus

@Component
@Provides
@Instantiate
class TraceServlet(
  @Requires httpService: HttpService,
  @Requires actorSystemProvider: ActorSystemProvider,
  @Requires archiver: Archiver) extends HttpServlet {

  private var actorSystem: ActorSystem = _
  private var currentState: ConcurrentHashMap[String, Any] = _
  private var mapper: ObjectMapper = _
  private var subscriber: ActorRef = _
  
  @Validate def start() = {

    val bus = archiver.bus()
    actorSystem = actorSystemProvider.getSystem()
    currentState = new ConcurrentHashMap[String, Any]()
    mapper = new ObjectMapper()
    subscriber = actorSystem.actorOf(
      Props(new ArchiveSubscriber(currentState)))

    httpService.registerServlet("/traces", this, null, null)
    bus.subscribe(
      subscriber.asInstanceOf[bus.Subscriber],
      classOf[ArchiverEvent[AnyRef]].asInstanceOf[bus.Classifier])
      
  }

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {

    val data = mapper.createArrayNode()
    currentState foreach {
      case (key, value) =>
        val datum = mapper.createObjectNode()
        datum.put("type", key)
        value match {
          case f: Float => datum.put("content", f)
          case o => datum.put("content", o.toString())
        }
        data.add(datum)
    }

    resp.setHeader("Access-Control-Allow-Origin", "*")
    resp.getWriter().write(data.toString())

  }

  @Invalidate def stop() = {
    val bus = archiver.bus()
    bus.unsubscribe(subscriber.asInstanceOf[bus.Subscriber])
    actorSystem.stop(subscriber)
  }

}

class ArchiveSubscriber(currentState: ConcurrentHashMap[String, Any])
  extends Actor {

  def receive = {

    case ae: ArchiverEvent[_] => currentState.put(ae.`type`, ae.content)
    case o => println("# improper event " + o + " in ArchiveSubscriber.")

  }

}