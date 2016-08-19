package com.example.chat

import akka.actor.ActorRef
import com.example.dbmodels.{ChatMessageRow, ChatUserRow}
import scalikejdbc.DBSession

trait ChatUserDBGateway {
  def exist(userId: String): Boolean
  def create(userId: String, name: String): Long
  def find(userId: String): Option[ChatUserRow]
}

trait ChatMessageDBGateway {
  def add(userId: String, body: String): Long
  def findRows(limit: Int): List[ChatMessageRow]
}

case class ChatSystemService(chatGroupActor: ActorRef)
(implicit dBSession: DBSession, chatUserDBGateway: ChatUserDBGateway, chatMessageDBGateway: ChatMessageDBGateway) {

  type DBRowId = Long

  def signUp(userId: String, name: String): Option[DBRowId] = {
    if (!chatUserDBGateway.exist(userId)) {
      val rowId = chatUserDBGateway.create(userId, name)
      Some(rowId)
    } else {
      None
    }
  }

  def findUser(userId: String): Option[ChatUserRow] = {
    chatUserDBGateway.find(userId)
  }

  def addMessage(userId: String, body: String): Option[DBRowId] = {
    if (chatUserDBGateway.exist(userId)) {
      Some(chatMessageDBGateway.add(userId, body))
    } else {
      None
    }
  }

  def findMessages(limit: Int): List[ChatMessageRow] = {
    chatMessageDBGateway.findRows(limit)
  }
}
