package com.example.dbmodels

import org.joda.time.DateTime
import scalikejdbc.DBSession
import skinny.orm.{Alias, SkinnyCRUDMapper}

case class ChatMessageRow(id: Long, userId: String, body: String, createAt: DateTime)

private case object ChatMessage extends SkinnyCRUDMapper[ChatMessageRow] {

  import scalikejdbc._

  override lazy val tableName = "chat_message"
  override lazy val defaultAlias: Alias[ChatMessageRow] = createAlias("m")

  override def extract(rs: WrappedResultSet, n: ResultName[ChatMessageRow]): ChatMessageRow =
    ChatMessageRow(
      id = rs.long(n.id),
      userId = rs.string(n.userId),
      body = rs.string(n.body),
      createAt = rs.jodaDateTime(n.createAt)
    )
}

case class ChatMessageService()(implicit dBSession: DBSession) {
  val chatUserService = ChatUserService()

  def add(userId: String, body: String): Option[Long] = {
    if(chatUserService.exist(userId)) {
      val id = ChatMessage.createWithAttributes('userId -> userId, 'body -> body)
      Some(id)
    } else {
      None
    }
  }

  def findAll(userId: String): List[ChatMessageRow] = {
    ChatMessage.where('userId -> userId).apply()
  }
}
