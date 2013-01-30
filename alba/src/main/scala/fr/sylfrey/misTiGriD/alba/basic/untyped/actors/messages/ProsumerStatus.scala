package fr.sylfrey.misTiGriD.alba.basic.untyped.actors.messages

import fr.sylfrey.misTiGriD.alba.basic.messages.ProsumerStatus

object GetStatus
case class SetStatus(status : ProsumerStatus)