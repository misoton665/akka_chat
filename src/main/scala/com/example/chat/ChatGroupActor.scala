package com.example.chat

import akka.actor.Actor
import com.example.chat.ChatSystemMessages._

class ChatGroupActor extends Actor {

  override def receive: Receive = {
    case msg@JoinedMessage(userId, participant) =>
      ChatMemberRepository.insertParticipant(userId, participant)
      broadcast(msg.toSystemMessage)

    case msg@LeftMessage(userId) =>
      ChatMemberRepository.deleteParticipant(userId)
      broadcast(msg.toSystemMessage)

    case msg@NewMessage(userId, body) =>
      broadcast(msg.toSystemMessage)

    case _ => ()
  }

  def broadcast(systemMessage: SystemMessage): Unit = {
    ChatMemberRepository.findParticipant(_ => true).foreach(_._2 ! systemMessage)
  }
}
