package com.example.chat

import com.example.chat.ChatMessages.{ChatMessage, ChatTextMessage, JoinMessage, LeftMessage}

object ChatMessageParsers {

  def hasDotAtPrefixWithMessage(text: String): (Boolean, String) = {
    val dotPrefixRegex = """\.(.*)""".r
    text match {
      case dotPrefixRegex(messageBody) => (true, messageBody)
      case _ => (false, text)
    }
  }

  def prefixExtractor(text: String): Option[String] = {
    val regex = """(.*) .*""".r
    text match {
      case regex(prefix) => Some(prefix)
      case _ => None
    }
  }

  def parse(userNameOpt: Option[String], groupNameOpt: Option[String], chatText: String): Option[ChatMessage] = {
    val (isNotChatText: Boolean, messageBody: String) = hasDotAtPrefixWithMessage(chatText)

    if (isNotChatText) {
      val parsers: List[ChatMessageParser[ChatMessage]] = List(JoinMessageParser, LeftMessageParser)
      val messagePrefix = prefixExtractor(chatText)
      val parserOpt: Option[ChatMessageParser[ChatMessage]] = parsers.find(_.messagePrefix == messagePrefix.getOrElse("undefined"))
      parserOpt.flatMap(_.parse(userNameOpt, groupNameOpt, chatText))
    } else {
      for (
        userName <- userNameOpt;
        groupName <- groupNameOpt
      ) yield {
        ChatTextMessage(userName, groupName, messageBody)
      }
    }
  }

  sealed trait ChatMessageParser[+T <: ChatMessage] {
    val messagePrefix: String
    val numberArgs: Int
    lazy val messageRegex = (messagePrefix + List.fill(numberArgs)(""" (.*)""").mkString("")).r

    def parse(userNameOpt: Option[String], groupNameOpt: Option[String], messageBody: String): Option[T]
  }

  case object JoinMessageParser extends ChatMessageParser[JoinMessage] {
    override val messagePrefix = "JOIN"
    override val numberArgs = 2

    override def parse(userNameOpt: Option[String], groupNameOpt: Option[String], messageBody: String) = {
      (userNameOpt, groupNameOpt) match {
        case (None, None) => messageBody match {
          case messageRegex(userName_, groupName_) => Some(JoinMessage(userName_, groupName_))
          case _ => None
        }
        case _ => None
      }
    }
  }

  case object LeftMessageParser extends ChatMessageParser[LeftMessage] {
    override val messagePrefix = "LEFT"
    override val numberArgs = 0

    override def parse(userNameOpt: Option[String], groupNameOpt: Option[String], messageBody: String) = {
      messageBody match {
        case messageRegex() => (userNameOpt, groupNameOpt) match {
          case (Some(userName), Some(groupName)) => Some(LeftMessage(userName, groupName))
          case _ => None
        }
        case _ => None
      }
    }
  }

}
