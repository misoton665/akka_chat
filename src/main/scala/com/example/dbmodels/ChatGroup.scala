package com.example.dbmodels

import org.joda.time.DateTime
import skinny.orm.{Alias, SkinnyCRUDMapper}

case class ChatGroup(id: Long, name: String, nickname: String, master: Long, createAt: DateTime)

case object ChatGroup extends SkinnyCRUDMapper[ChatGroup] {

  import scalikejdbc._

  override val tableName = "chat_group"
  override def defaultAlias: Alias[ChatGroup] = createAlias("g")

  override def extract(rs: WrappedResultSet, n: ResultName[ChatGroup]): ChatGroup =
    ChatGroup (
      id = rs.long(n.id),
      name = rs.string(n.name),
      nickname =  rs.string(n.nickname),
      master = rs.long(n.master),
      createAt = rs.jodaDateTime(n.createAt)
    )
}
