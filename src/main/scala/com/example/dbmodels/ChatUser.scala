package com.example.dbmodels

import org.joda.time.DateTime
import scalikejdbc.DBSession
import skinny.orm.{Alias, SkinnyCRUDMapper}

case class ChatUser(id: Long, userId: String, name: String, createAt: Option[DateTime])

private case object ChatUser extends SkinnyCRUDMapper[ChatUser] {

  import scalikejdbc._

  override lazy val tableName = "chat_user"
  override lazy val defaultAlias: Alias[ChatUser] = createAlias("u")

  override def extract(rs: WrappedResultSet, n: ResultName[ChatUser]): ChatUser =
    ChatUser(
      id = rs.long(n.id),
      userId = rs.string(n.userId),
      name = rs.string(n.name),
      createAt = rs.jodaDateTimeOpt(n.createAt)
    )
}

case class ChatUserService()(implicit dBSession: DBSession) {

  def exist(userId: String): Boolean = {
    val user: ChatUser = ChatUser.where('userId -> userId).apply().head
    ChatUser.where('userId -> userId).count('userId) >= 1
  }

  def createUser(userId: String, name: String): Long = {
    ChatUser.createWithAttributes('userId -> userId, 'name -> name)
  }
}