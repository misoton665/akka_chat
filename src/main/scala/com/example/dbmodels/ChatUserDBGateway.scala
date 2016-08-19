package com.example.dbmodels

import com.example.chat.ChatUserDBGateway
import org.joda.time.DateTime
import scalikejdbc.DBSession
import skinny.orm.{Alias, SkinnyCRUDMapper}

case class ChatUserRow(id: Option[Long], userId: String, name: String, createAt: Option[DateTime])

private case object ChatUser extends SkinnyCRUDMapper[ChatUserRow] {

  import scalikejdbc._

  override lazy val tableName = "chat_user"
  override lazy val defaultAlias: Alias[ChatUserRow] = createAlias("u")

  override def extract(rs: WrappedResultSet, n: ResultName[ChatUserRow]): ChatUserRow =
    ChatUserRow(
      id = rs.longOpt(n.id),
      userId = rs.string(n.userId),
      name = rs.string(n.name),
      createAt = rs.jodaDateTimeOpt(n.createAt)
    )
}

case class ChatUserDBGatewayImpl()(implicit dBSession: DBSession) extends ChatUserDBGateway {

  override def exist(userId: String): Boolean = {
    ChatUser.where('userId -> userId).count('userId) >= 1
  }

  override def create(userId: String, name: String): Long = {
    ChatUser.createWithAttributes('userId -> userId, 'name -> name)
  }

  override def find(userId: String): Option[ChatUserRow] = {
    ChatUser.where('userId -> userId).apply().headOption
  }
}
