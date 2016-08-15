package com.example.dbmodels

import org.joda.time.DateTime
import skinny.orm.{Alias, SkinnyCRUDMapper}

case class ChatMessage(id: Long, userId: String, body: String, createAt: DateTime)

case object ChatMessage extends SkinnyCRUDMapper[ChatMessage] {

  import scalikejdbc._

  override lazy val tableName = "chat_message"
  override lazy val defaultAlias: Alias[ChatMessage] = createAlias("m")

  override def extract(rs: WrappedResultSet, n: ResultName[ChatMessage]): ChatMessage =
    ChatMessage(
      id = rs.long(n.id),
      userId = rs.string(n.userId),
      body = rs.string(n.body),
      createAt = rs.jodaDateTime(n.createAt)
    )
}
