package com.example.routes

import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Flow

object ChatRoute extends RouteExtractable {

  import akka.http.scaladsl.server.Directives._

  def chatHandler(groupName: String, userName: String): Flow[Message, Message, _] = Flow[Message].map {
    case TextMessage.Strict(text) => TextMessage(s"[-] @$userName: " + text + s" to GROUP: $groupName")
    case _ => TextMessage(s"[!] @$userName: Message type unsupported.")
  }

  override def route: Route =
    (get & path(Segment) & parameter('name)) { (groupName, userName) =>
      handleWebSocketMessages(chatHandler(groupName, userName))
    }
}
