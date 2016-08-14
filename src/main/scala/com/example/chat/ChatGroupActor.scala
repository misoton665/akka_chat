package com.example.chat

import akka.actor.{Actor, ActorRef}
import com.example.chat.ChatSystemMessages._

class ChatGroupActor extends Actor {

  var participants: Map[String, ActorRef] = Map.empty[String, ActorRef]

  override def receive: Receive = {
    case msg@JoinedMessage(userId, participant) =>
      participants += userId -> participant
      broadcast(msg.toSystemMessage)

    case msg@LeftMessage(userId) =>
      participants -= userId
      broadcast(msg.toSystemMessage)

    case msg@NewMessage(userId, body) =>
      broadcast(msg.toSystemMessage)

    case _ => ()
  }

  def broadcast(systemMessage: SystemMessage): Unit = {
    participants.foreach(_._2 ! systemMessage)
  }
}
