package com.example.routes

import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.{FlowShape, Materializer, OverflowStrategy}
import akka.stream.scaladsl.{Flow, GraphDSL, Merge, Sink, Source}
import com.example.chat.{ChatSystemMessages, ChatSystemService}
import com.example.chat.ChatSystemMessages._

case class ChatStreamHandler()(implicit chatSystemService: ChatSystemService, materializer: Materializer) {

  def handler(userId: String): Flow[Message, Message, _] =
    Flow.fromGraph(GraphDSL.create(Source.actorRef[SystemMessage](5, OverflowStrategy.fail)) {
      implicit builder =>
        participantSource =>
          import GraphDSL.Implicits._
          val fromClientFlow = builder.add(Flow[Message].collect {
            case TextMessage.Strict(txt) => NewMessage(userId, MessageBody(txt))
          })

          val merge = builder.add(Merge[ChatSystemMessage](2))

          val toClientFlow = builder.add(Flow[SystemMessage].map[Message] {
            case SystemMessage(body) => TextMessage.Strict(body)
          })

          val actorMaterializedSource = builder.materializedValue.map {
            JoinedMessage(userId, _)
          }

          def toChatGroupActorSink(userId: String) =
            Sink.actorRef[ChatSystemMessages.ChatSystemMessage](chatSystemService.chatGroupActor, LeftMessage(userId))

          fromClientFlow ~> merge ~> toChatGroupActorSink(userId)
          actorMaterializedSource ~> merge

          participantSource ~> toClientFlow

          FlowShape(fromClientFlow.in, toClientFlow.out)
    })
}
