package com.example.chat

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.scaladsl.Sink
import com.example.chat.ChatSystemMessages.LeftMessage

case class ChatSystem()(implicit actorSystem: ActorSystem) {
  val chatActor = actorSystem.actorOf(Props[ChatGroupActor])

  def chatLeftSink(userId: String) = Sink.actorRef[ChatSystemMessages.ChatSystemMessage](chatActor, LeftMessage(userId))
}

object ChatSystemMessages {

  sealed trait ChatSystemMessage {
    val shortMessage: String
  }

  case class MessageBody(value: String)

  case class JoinedMessage(userId: String, member: ActorRef) extends ChatSystemMessage {
    override val shortMessage: String = "JOIN"
  }

  case class LeftMessage(userId: String) extends ChatSystemMessage {
    override val shortMessage: String = "LEFT"
  }

  case class NewMessage(userId: String, body: MessageBody) extends ChatSystemMessage {
    override val shortMessage: String = "NEW MESSAGE"
  }

  case class SystemMessage(userId: String, body: String)
}
