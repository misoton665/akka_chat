package com.example.chat

import akka.actor.{Actor, ActorRef}
import com.example.chat.ChatSystemMessages._

class ChatGroupActor extends Actor{

  var participants: Map[String, ActorRef] = Map.empty[String, ActorRef]

  override def receive: Receive = {
    case JoinedMessage(userId, participant) =>
      participants += userId -> participant
      broadcast(SystemMessage(userId, s"[JOINED] @$userId"))

    case LeftMessage(userId) =>
      participants -= userId
      broadcast(SystemMessage(userId, s"[ LEFT ] @$userId"))

    case NewMessage(userId, body) =>
      broadcast(SystemMessage(userId, s"@$userId | ${body.value}"))

    case _ => ()
  }

  def broadcast(systemMessage: SystemMessage): Unit = {
    participants.foreach(_._2 ! systemMessage)
  }
}
