package com.example.chat

import akka.actor.ActorRef
import com.example.dbmodels.ChatMessageRow

object ChatSystemMessages {

  sealed trait ChatSystemMessage {
    val toSystemMessage: SystemMessage
  }

  case class MessageBody(value: String)

  case class JoinedMessage(userId: String, member: ActorRef) extends ChatSystemMessage {
    override val toSystemMessage: SystemMessage = SystemMessage(s"[JOINED] @$userId")
  }

  case class LeftMessage(userId: String) extends ChatSystemMessage {
    override val toSystemMessage: SystemMessage = SystemMessage(s"[ LEFT ] @$userId")
  }

  case class NewMessage(userId: String, body: MessageBody) extends ChatSystemMessage {
    override val toSystemMessage: SystemMessage = SystemMessage(s"@$userId ${body.value}")
  }

  case class ReportMessage(messages: List[ChatMessageRow], userActor: ActorRef) extends ChatSystemMessage {
    private val chatMessagesText = messages.map{m => s"@${m.userId} ${m.body}"}.mkString("\n")

    override val toSystemMessage: SystemMessage =
      SystemMessage(s"last ${messages.size} messages\n$chatMessagesText")
  }

  case class RejectMessage(userId: String, userActor: ActorRef) extends ChatSystemMessage {
    override val toSystemMessage: SystemMessage = SystemMessage(s"@$userId is not found.")
  }

  case class SystemMessage(body: String)
}
