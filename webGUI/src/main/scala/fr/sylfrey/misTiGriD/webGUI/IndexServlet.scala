package fr.sylfrey.misTiGriD.webGUI

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest

class IndexServlet(registry : GenericLayoutRegistry) extends HttpServlet {

  override def doGet(req : HttpServletRequest, resp : HttpServletResponse ) : Unit = {
    resp.getWriter().write(Serialiser.index(registry.layouts))		
  }	
}