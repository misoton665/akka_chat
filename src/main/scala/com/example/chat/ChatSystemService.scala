package com.example.chat

import com.example.dbmodels.ChatUser
import scalikejdbc.DBSession

case class ChatSystemService()(implicit dBSession: DBSession) {
  def signUp(userId: String, name: String): String = {
    if (ChatUser.where('userId -> userId).count('userId) == 0) {
      ChatUser.createWithAttributes('userId -> userId, 'name -> name)
      s"signUp by $userId, $name"
    } else {
      s"$userId was already signed up"
    }
  }
}
