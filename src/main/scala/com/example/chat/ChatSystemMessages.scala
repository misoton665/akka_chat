package com.example.chat

import akka.actor.ActorRef

object ChatSystemMessages {

  sealed trait ChatSystemMessage {
    val toSystemMessage: SystemMessage
  }

  case class MessageBody(value: String)

  case class JoinedMessage(userId: String, member: ActorRef) extends ChatSystemMessage {
    override val toSystemMessage: SystemMessage = SystemMessage(userId, "[JOINED] @$userId")
  }

  case class LeftMessage(userId: String) extends ChatSystemMessage {
    override val toSystemMessage: SystemMessage = SystemMessage(userId, s"[ LEFT ] @$userId")
  }

  case class NewMessage(userId: String, body: MessageBody) extends ChatSystemMessage {
    override val toSystemMessage: SystemMessage = SystemMessage(userId, s"@$userId $body")
  }

  case class SystemMessage(userId: String, body: String)

}
