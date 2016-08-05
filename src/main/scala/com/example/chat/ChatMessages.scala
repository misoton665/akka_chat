package com.example.chat

object ChatMessages {
  sealed trait ChatMessage

  case class ChatTextMessage(userName: String, groupName: String, body: String) extends ChatMessage

  case class JoinMessage(userName: String, groupName: String) extends ChatMessage

  case class LeftMessage(userName: String, groupName: String) extends ChatMessage
}


