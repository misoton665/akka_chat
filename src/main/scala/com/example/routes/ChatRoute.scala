package com.example.routes

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Route
import akka.stream.{ClosedShape, FlowShape, Materializer, OverflowStrategy}
import akka.stream.scaladsl.{Flow, GraphDSL, Merge, Sink, Source}
import com.example.chat.{ChatMessageParsers, ChatSystem, ChatSystemMessages, MessageBody}
import com.example.chat.ChatSystemMessages._

case class ChatRoute(actorSystem: ActorSystem, materializer: Materializer) extends RouteExtractable {

  import akka.http.scaladsl.server.Directives._

  def handler(userId: String): Flow[Message, Message, _] = Flow.fromGraph(GraphDSL.create(Source.actorRef[SystemMessage](5, OverflowStrategy.fail)) {
    implicit builder =>
      chatSource =>
        import GraphDSL.Implicits._
        val fromWebsocket = builder.add(Flow[Message].collect {
          case TextMessage.Strict(txt) => NewMessage(userId, MessageBody(txt))
        })

        val merge = builder.add(Merge[ChatSystemMessage](2))

        val backToWebsocket = builder.add(Flow[SystemMessage].map[Message] { m => TextMessage.Strict(m.body) })


        val actorAsSource = builder.materializedValue.map {
          JoinedMessage(userId, _)
        }
        //          Source.actorRef[ChatSystemMessage](5, OverflowStrategy.fail).mapMaterializedValue {
        //          chatSystem.chatActor ! JoinedMessage(userId, _)
        //        }

        fromWebsocket ~> merge ~> chatSystem.chatLeftSink(userId)
        actorAsSource ~> merge
        chatSource ~> backToWebsocket
        FlowShape(fromWebsocket.in, backToWebsocket.out)
  })

  val chatSystem = new ChatSystem(actorSystem)

  override def route: Route =
    (get & parameter('name)) { (userId) =>
      handleWebSocketMessages(handler(userId))
    }
}
