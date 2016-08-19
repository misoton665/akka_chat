package com.example.chat

import akka.actor.ActorRef
import com.example.dbmodels.{ChatMessageRow, ChatMessageDBGateway, ChatUserRow, ChatUserDBGateway}
import scalikejdbc.DBSession

case class ChatSystemService(chatGroupActor: ActorRef)(implicit dBSession: DBSession) {
  private val chatUserGateway = ChatUserDBGateway()
  private val chatMessageGateway = ChatMessageDBGateway()

  type DBRowId = Long

  def signUp(userId: String, name: String): Option[DBRowId] = {
    if (!chatUserGateway.exist(userId)) {
      val rowId = chatUserGateway.create(userId, name)
      Some(rowId)
    } else {
      None
    }
  }

  def findUser(userId: String): Option[ChatUserRow] = {
    chatUserGateway.find(userId)
  }

  def addMessage(userId: String, body: String): Option[DBRowId] = {
    if (chatUserGateway.exist(userId)) {
      Some(chatMessageGateway.add(userId, body))
    } else {
      None
    }
  }

  def findMessages(limit: Int): List[ChatMessageRow] = {
    chatMessageGateway.findRows(limit)
  }
}
