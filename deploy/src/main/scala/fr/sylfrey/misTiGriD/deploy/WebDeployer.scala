package fr.sylfrey.misTiGriD.deploy

//import java.util.{List => JList}
//import scala.collection.JavaConversions._
//import scala.collection.mutable.ListBuffer
//import scala.concurrent.ExecutionContext
//import org.apache.felix.ipojo.annotations.Component
//import org.apache.felix.ipojo.annotations.Instantiate
//import org.apache.felix.ipojo.annotations.Requires
//import org.apache.felix.ipojo.annotations.Validate
//import org.codehaus.jackson.map.ObjectMapper
//import org.osgi.service.http.HttpService
//import org.osgi.service.http.NamespaceException
//import javax.servlet.Servlet
//import javax.servlet.http.HttpServletRequest
//import javax.servlet.http.HttpServletResponse
//import org.codehaus.jackson.node.ArrayNode
//import org.codehaus.jackson.node.ObjectNode
//import org.codehaus.jackson.JsonNode
//import javax.servlet.http.HttpServlet
//import org.apache.felix.ipojo.ComponentInstance

//@Component
//@Instantiate
class WebDeployer {

//  @Requires var httpService: HttpService = _
//  @Requires var metaFactory : MetaFactory = _
//  val path = "/webdeploy"
//  val mapper : ObjectMapper  = new ObjectMapper()
//
//  @Validate def start = {
//    try {
//      httpService.registerResources(path, "/WebDeploy", null)
//    } catch {
//      case e: NamespaceException => e.printStackTrace()
//    }
//
//    try {
//      httpService.registerServlet(path + "/servlet", new DeployServlet(), null, null)
//    } catch {
//      case e: NamespaceException => e.printStackTrace()
//    }
//  }
//  
//  class DeployServlet extends HttpServlet {
//    
//    implicit val ec = ExecutionContext.Implicits.global
//    
//    override def doGet(req: HttpServletRequest, resp: HttpServletResponse) {
//      val factories = mapper.createArrayNode()
//      metaFactory.factories.keys.foreach(factories.add(_))
//      resp.getWriter.write(factories.toString())
//    }
//    
//    override def doPost(req: HttpServletRequest, resp: HttpServletResponse) {
//      val path = req.getPathInfo() 
//      println("# path = " + path)
//      if (path != null) path.split("/") match { 
//        case Array(factoryId) => {
//          if (!metaFactory.factories.keySet().contains(factoryId)) {
//            println("# unknown factory")
//            resp.getWriter.write("# unknown factory : " + factoryId)
//            return
//          }
//          val body = req.getParameter("body")
//          val config = parse(mapper.readValue(body, classOf[JsonNode]))
//          val instance = metaFactory.spawn(factoryId, config:_*).future
//          instance onSuccess {
//            case instance : ComponentInstance => resp.getWriter.write("# instance name : " + instance.getInstanceName())
//            case null => resp.getWriter.write("# problem, factory returned null")
//          }
//          instance onFailure {
//            case throwable => resp.getWriter.write("# problem, factory returned " + throwable)
//          }
//        }
//        case wrongPath => resp.getWriter.write("# wrong path : " + wrongPath)
//      }
//    }
//    
//  }
//  
//  def parse(json: JsonNode): List[(String, _)] = {
//    val result = ListBuffer[(String, _)]()
//    json.getFields.foreach { entry => result += ((entry.getKey, parse(entry.getValue))) }
//    result.toList
//  }  

}