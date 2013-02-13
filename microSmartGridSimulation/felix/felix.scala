import org.apache.felix.framework._
import java.util.HashMap
import scala.io.Source

val props = new HashMap[String,String]()
val conf = Source.fromFile("conf/config.properties")
conf.getLines() foreach { line => 
  if (!line.startsWith("#") && !line.isEmpty()) {
    val prop = line.split("=")
    props.put(prop(0), prop(1)) 
  }
}
val felix = new Felix(props)
felix.start