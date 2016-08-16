package com.example.chat

import com.example.dbmodels.ChatUserService
import scalikejdbc.DBSession

case class ChatSystemService()(implicit dBSession: DBSession) {
  private val chatUserService = ChatUserService()

  def signUp(userId: String, name: String): Option[Long] = {
    if (chatUserService.exist(userId)) {
      val tableId = chatUserService.createUser(userId, name)
      Some(tableId)
    } else {
      None
    }
  }
}
