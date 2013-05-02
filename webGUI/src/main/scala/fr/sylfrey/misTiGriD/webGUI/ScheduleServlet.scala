package fr.sylfrey.misTiGriD.webGUI

import fr.sylfrey.misTiGriD.alba.basic.model.Schedule
import org.codehaus.jackson.node.ObjectNode
import org.codehaus.jackson.map.ObjectMapper
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletResponse

class ScheduleServlet(schedule: Schedule) extends HttpServlet {

  override def doGet(request: HttpServletRequest, response: HttpServletResponse) = {
    response.setContentType("application/json")
    response.setStatus(HttpServletResponse.SC_OK)
    response.getWriter().println(json(schedule))
  }

  val mapper = new ObjectMapper()
  
  private def json(schedule: Schedule): String = {

    val data = mapper.createObjectNode()

    //device prosumptions
    val series = mapper.createObjectNode()

    schedule.packets.foreach {
      case (packet, start) =>

        //println("# rendering " + packet + " @ " + start)
        val serie = mapper.createObjectNode()
        serie.put("status", packet.status.toString())

        val points = mapper.createObjectNode()
        for (t <- schedule.schedule.indices) {
          if (schedule.get(t).contains(packet)) {
            points.put(t.toString, packet.max)
          } else {
            points.put(t.toString, 0)
          }
        }
        serie.put("points", points)

        val baseSerieName = packet.device.path.toString().split("/").last
        series.put(unique(baseSerieName, series), serie)

    }

    data.put("schedule", series)

    // goal
    val goalSerie = mapper.createObjectNode()
    schedule.goal.indices.foreach { x =>
      goalSerie.put(x.toString, schedule.goal(x))
    }
    data.put("goal", goalSerie)
    
    data.put("scheduleSize", schedule.size)

    data.toString

  }
  
  def unique(serieName: String, series: ObjectNode): String = {
    if (series.get(serieName)==null) serieName
    else unique(serieName + "'", series)
  }

}