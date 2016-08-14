package com.example.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.{FlowShape, Materializer, OverflowStrategy}
import akka.stream.scaladsl.{Flow, GraphDSL, Merge, Source}
import com.example.chat.ChatSystem
import com.example.chat.ChatSystemMessages._

case class ChatStreamHandler()(implicit actorSystem: ActorSystem, materializer: Materializer) {

  val chatSystem = ChatSystem()

  def handler(userId: String): Flow[Message, Message, _] =
    Flow.fromGraph(GraphDSL.create(Source.actorRef[SystemMessage](5, OverflowStrategy.fail)) {
    implicit builder =>
      chatSource =>
        import GraphDSL.Implicits._
        val fromWebsocket = builder.add(Flow[Message].collect {
          case TextMessage.Strict(txt) => NewMessage(userId, MessageBody(txt))
        })

        val merge = builder.add(Merge[ChatSystemMessage](2))

        val backToWebsocket = builder.add(Flow[SystemMessage].map[Message] {
          case SystemMessage(_, body) => TextMessage.Strict(body)
        })

        val actorAsSource = builder.materializedValue.map {
          JoinedMessage(userId, _)
        }

        fromWebsocket ~> merge ~> chatSystem.chatLeftSink(userId)
        actorAsSource ~> merge
        chatSource ~> backToWebsocket

        FlowShape(fromWebsocket.in, backToWebsocket.out)
  })
}
