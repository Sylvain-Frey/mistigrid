package fr.sylfrey.misTiGriD.deploy.management.alba.basic

import fr.sylfrey.misTiGriD.alba.basic.messages.ProsumerStatus
import fr.sylfrey.misTiGriD.alba.basic.messages.Flexible
import fr.sylfrey.misTiGriD.alba.basic.messages.SemiFlexible
import fr.sylfrey.misTiGriD.alba.basic.messages.NonFlexible

object StatusDecoder {
	def decode(status : String) : ProsumerStatus = status.toLowerCase() match {
      case "flexible" => Flexible
      case "semiflexible" => SemiFlexible
      case "nonflexible" => NonFlexible
      case _ => NonFlexible
	}
}