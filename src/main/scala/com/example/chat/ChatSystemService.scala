package com.example.chat

import akka.actor.ActorRef
import com.example.dbmodels.{ChatMessageRow, ChatMessageService, ChatUserService}
import scalikejdbc.DBSession

case class ChatSystemService(chatGroupActor: ActorRef)(implicit dBSession: DBSession) {
  private val chatUserService = ChatUserService()
  private val chatMessageService = ChatMessageService()

  type DBRowId = Long

  def signUp(userId: String, name: String): Option[DBRowId] = {
    if (!chatUserService.exist(userId)) {
      val rowId = chatUserService.create(userId, name)
      Some(rowId)
    } else {
      None
    }
  }

  def addMessage(userId: String, body: String): Option[DBRowId] = {
    if (chatUserService.exist(userId)) {
      Some(chatMessageService.add(userId, body))
    } else {
      None
    }
  }

  def findMessages(limit: Int): List[ChatMessageRow] = {
    chatMessageService.findRows(limit)
  }
}
