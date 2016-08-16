package com.example.chat

import akka.actor.ActorRef
import com.example.dbmodels.{ChatMessageService, ChatUserService}
import scalikejdbc.DBSession

case class ChatSystemService(chatGroupActor: ActorRef)(implicit dBSession: DBSession) {
  private val chatUserService = ChatUserService()
  private val chatMessageService = ChatMessageService()

  def signUp(userId: String, name: String): Option[Long] = {
    if (!chatUserService.exist(userId)) {
      val tableId = chatUserService.create(userId, name)
      Some(tableId)
    } else {
      None
    }
  }

  def addMessage(userId: String, body: String): Option[Long] = {
    if (chatUserService.exist(userId)) {
      Some(chatMessageService.add(userId, body))
    } else {
      None
    }
  }
}
