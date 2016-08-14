package com.example.chat

import akka.actor.{ActorSystem, Props}
import akka.stream.scaladsl.Sink
import com.example.chat.ChatSystemMessages.LeftMessage

case class ChatSystem()(implicit actorSystem: ActorSystem) {
  val chatGroupActor = actorSystem.actorOf(Props[ChatGroupActor])

  def toChatGroupActorSink(userId: String) =
    Sink.actorRef[ChatSystemMessages.ChatSystemMessage](chatGroupActor, LeftMessage(userId))
}
