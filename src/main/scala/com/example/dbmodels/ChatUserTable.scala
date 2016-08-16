package com.example.dbmodels

import org.joda.time.DateTime
import scalikejdbc.DBSession
import skinny.orm.{Alias, SkinnyCRUDMapper}

case class ChatUserRow(id: Long, userId: String, name: String, createAt: DateTime)

private case object ChatUser extends SkinnyCRUDMapper[ChatUserRow] {

  import scalikejdbc._

  override lazy val tableName = "chat_user"
  override lazy val defaultAlias: Alias[ChatUserRow] = createAlias("u")

  override def extract(rs: WrappedResultSet, n: ResultName[ChatUserRow]): ChatUserRow =
    ChatUserRow(
      id = rs.long(n.id),
      userId = rs.string(n.userId),
      name = rs.string(n.name),
      createAt = rs.jodaDateTime(n.createAt)
    )
}

case class ChatUserService()(implicit dBSession: DBSession) {

  def exist(userId: String): Boolean = {
    ChatUser.where('userId -> userId).count('userId) >= 1
  }

  def create(userId: String, name: String): Long = {
    ChatUser.createWithAttributes('userId -> userId, 'name -> name)
  }

  def find(userId: String): Option[ChatUserRow] = {
    ChatUser.where('userId -> userId).apply().headOption
  }
}