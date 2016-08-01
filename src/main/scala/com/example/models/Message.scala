package com.example.models

import org.joda.time.DateTime
import skinny.orm.{Alias, SkinnyCRUDMapper}

case class Message(id: Long, userId: Option[Long], groupId: Option[Long], body: Option[String], sendDate: DateTime)

case object Message extends SkinnyCRUDMapper[Message] {

  import scalikejdbc._

  override lazy val tableName = "message"
  override lazy val defaultAlias: Alias[Message] = createAlias("m")

  override def extract(rs: WrappedResultSet, n: ResultName[Message]): Message =
    Message(
      id = rs.long(n.id),
      userId = rs.longOpt(n.userId),
      groupId = rs.longOpt(n.groupId),
      body = rs.stringOpt(n.body),
      sendDate = rs.jodaDateTime(n.sendDate)
    )
}
