import java.util.{ List => JList }
import scala.collection.JavaConversions._
import fr.tpt.s3.misTiGriD.conf._

:load deploy/house.scala

$[HouseFactory].make(
      House.atmosphereModel,
      House.aggregator,
      House.walls, House.wallLayouts,
      House.rooms, House.roomLayouts,
      House.heaters, House.heaterLayouts)
      
:load deploy/extra.scala