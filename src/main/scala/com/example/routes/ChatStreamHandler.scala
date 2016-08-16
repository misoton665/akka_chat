package com.example.routes

import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.{FlowShape, Materializer, OverflowStrategy}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge, Sink, Source}
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

          val merge = builder.add(Merge[ChatSystemMessage](3))

          val broadcast = builder.add(Broadcast[ChatSystemMessage](2))

          val toClientFlow = builder.add(Flow[SystemMessage].map[Message] {
            case SystemMessage(body) => TextMessage.Strict(body)
          })

          val actorMaterializedSource = builder.materializedValue.map {
            actor => chatSystemService.findUser(userId) match {
              case Some(_) => JoinedMessage(userId, actor)
              case None => RejectMessage(userId, actor)
            }
          }

          val reportLastMessageFlow = builder.add(Flow[ChatSystemMessage].map {
            case JoinedMessage(_, userActor) => ReportMessage(chatSystemService.findMessages(20), userActor)
            case m@_ => m
          })

          val registerMessageFlow = builder.add(Flow[ChatSystemMessage].map {
            case msg@NewMessage(_userId, body) =>
              chatSystemService.addMessage(_userId, body.value)
              msg
            case msg@_ => msg
          })

          def toChatGroupActorSink(userId: String) =
            Sink.actorRef[ChatSystemMessages.ChatSystemMessage](chatSystemService.chatGroupActor, LeftMessage(userId))

          actorMaterializedSource ~> broadcast

          broadcast ~> reportLastMessageFlow ~> merge
          broadcast ~> merge
          fromClientFlow ~> registerMessageFlow ~> merge ~> toChatGroupActorSink(userId)

          participantSource ~> toClientFlow

          FlowShape(fromClientFlow.in, toClientFlow.out)
    })
}
