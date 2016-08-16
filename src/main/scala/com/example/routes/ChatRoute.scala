package com.example.routes

import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import com.example.chat.ChatSystemService

case class ChatRoute()(implicit chatSystemService: ChatSystemService, materializer: Materializer) extends RouteExtractable {

  import akka.http.scaladsl.server.Directives._

  val chatStreamHandler = ChatStreamHandler()

  override def route: Route =
    (get & parameter('name)) { (userId) =>
      handleWebSocketMessages(chatStreamHandler.handler(userId))
    }
}
