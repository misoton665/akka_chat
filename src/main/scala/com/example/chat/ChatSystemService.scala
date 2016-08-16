package com.example.chat

import com.example.dbmodels.ChatUser
import scalikejdbc.DBSession

case class ChatSystemService()(implicit dBSession: DBSession) {
  def signUp(userId: String, name: String): Option[Long] = {
    if (ChatUser.where('userId -> userId).count('userId) == 0) {
      val tableId = ChatUser.createWithAttributes('userId -> userId, 'name -> name)
      Some(tableId)
    } else {
      None
    }
  }
}
