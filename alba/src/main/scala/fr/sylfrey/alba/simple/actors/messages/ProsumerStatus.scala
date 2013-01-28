package fr.sylfrey.alba.simple.actors.messages

import fr.sylfrey.alba.simple.ProsumerStatus

object GetStatus
case class SetStatus(status : ProsumerStatus)