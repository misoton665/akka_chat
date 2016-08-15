package com.example.dbmodels

import org.joda.time.DateTime
import skinny.orm.{Alias, SkinnyCRUDMapper}

case class ChatUser(id: Long, userId: String, name: String, createAt: DateTime)

case object ChatUser extends SkinnyCRUDMapper[ChatUser] {

  import scalikejdbc._

  override lazy val tableName = "chat_user"
  override lazy val defaultAlias: Alias[ChatUser] = createAlias("u")

  override def extract(rs: WrappedResultSet, n: ResultName[ChatUser]): ChatUser =
    ChatUser(
      id = rs.long(n.id),
      userId = rs.string(n.userId),
      name = rs.string(n.name),
      createAt = rs.jodaDateTime(n.createAt)
    )
}
