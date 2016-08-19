package com.example.dbmodels

import com.example.chat.ChatMessageDBGateway
import org.joda.time.DateTime
import scalikejdbc.DBSession
import skinny.orm.{Alias, SkinnyCRUDMapper}

case class ChatMessageRow(id: Long, userId: String, body: String, createAt: DateTime)

private case object ChatMessageCRUD extends SkinnyCRUDMapper[ChatMessageRow] {

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

case class ChatMessageDBGatewayImpl()(implicit dBSession: DBSession) extends ChatMessageDBGateway {

  override def add(userId: String, body: String): Long = {
    ChatMessageCRUD.createWithAttributes('userId -> userId, 'body -> body)
  }

  override def findRows(limit: Int): List[ChatMessageRow] = {
    ChatMessageCRUD.limit(limit).apply()
  }
}
