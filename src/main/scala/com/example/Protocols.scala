package com.example

import com.example.models.{ChatGroup, ChatUser, Message}
import spray.json.DefaultJsonProtocol

trait Protocols extends DefaultJsonProtocol {
  implicit val chatUserFormat = jsonFormat5(ChatUser.apply)
  implicit val chatGroupFormat = jsonFormat5(ChatGroup.apply)
  implicit val messageFormat = jsonFormat5(Message.apply)
}
