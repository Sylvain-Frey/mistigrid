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
package fr.sylfrey.misTiGriD.webGUI

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest

class IndexServlet(registry : GenericLayoutRegistry) extends HttpServlet {

  override def doGet(req : HttpServletRequest, resp : HttpServletResponse ) : Unit = {
    println("# " + req.getRemoteAddr() + " GET : " + req.getPathInfo())
    resp.getWriter().write(Serialiser.index(registry.layouts))		
  }	
  
}
