package com.example.dbmodels

import org.joda.time.DateTime
import skinny.orm.{Alias, SkinnyCRUDMapper}

case class ChatUser(id: Long, name: Option[String], nickname: Option[String], bio: Option[String], signUpDate: DateTime)

case object ChatUser extends SkinnyCRUDMapper[ChatUser] {

  import scalikejdbc._

  override lazy val tableName = "chat_user"
  override lazy val defaultAlias: Alias[ChatUser] = createAlias("u")

  override def extract(rs: WrappedResultSet, n: ResultName[ChatUser]): ChatUser =
    ChatUser(
      id = rs.long(n.id),
      name = rs.stringOpt(n.name),
      nickname = rs.stringOpt(n.nickname),
      bio = rs.stringOpt(n.bio),
      signUpDate = rs.jodaDateTime(n.signUpDate)
    )
}
