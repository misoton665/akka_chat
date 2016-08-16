package com.example.chat

import com.example.dbmodels.{ChatMessageService, ChatUserService}
import scalikejdbc.DBSession

case class ChatSystemService()(implicit dBSession: DBSession) {
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
    chatMessageService.add(userId, body)
  }
}
