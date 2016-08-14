package com.example.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.stream.Materializer

case class ChatRoute()(implicit actorSystem: ActorSystem, materializer: Materializer) extends RouteExtractable {

  import akka.http.scaladsl.server.Directives._

  val chatStreamHandler = ChatStreamHandler()

  override def route: Route =
    (get & parameter('name)) { (userId) =>
      handleWebSocketMessages(chatStreamHandler.handler(userId))
    }
}
