package com.example.chat

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.{FlowShape, OverflowStrategy, SinkShape, SourceShape}
import akka.stream.scaladsl.{Flow, GraphDSL, Sink, Source}
import com.example.chat.ChatSystemMessages.{JoinedMessage, LeftMessage, NewMessage}

class ChatSystem(actorSystem: ActorSystem) {
//  var chatGroups: Map[String, ChatGroupActor] = Map.empty[String, ChatGroupActor]

  val chatActor = actorSystem.actorOf(Props[ChatGroupActor])

  def chatLeftSink(userId: String) = Sink.actorRef[ChatSystemMessages.ChatSystemMessage](chatActor, LeftMessage(userId))

  def messageHandler(sender: String): Flow[MessageBody, ChatSystemMessages.ChatSystemMessage, _] = {
    val in = Sink.fromGraph(GraphDSL.create() {
      implicit builder =>
        import GraphDSL.Implicits._

        val toNewMessage = builder.add(Flow[MessageBody].map{NewMessage(sender, _).asInstanceOf[ChatSystemMessages.ChatSystemMessage]})

        val chatSink = builder.add(chatLeftSink(sender))

        toNewMessage ~> chatSink
        SinkShape.of(toNewMessage.in)
    })

    val out =
      Source.actorRef[ChatSystemMessages.ChatSystemMessage](5, OverflowStrategy.fail)
        .mapMaterializedValue{
          chatActor ! JoinedMessage(sender, _)
        }

    Flow.fromSinkAndSource(in, out)
  }
}

case class MessageBody(value: String)

object ChatSystemMessages {

  sealed trait ChatSystemMessage {
    val shortMessage: String
  }

  case class JoinedMessage(userId: String, member: ActorRef) extends ChatSystemMessage {
    override val shortMessage: String = "JOIN"
  }

  case class LeftMessage(userId: String) extends ChatSystemMessage {
    override val shortMessage: String = "LEFT"
  }

  case class NewMessage(userId: String, body: MessageBody) extends ChatSystemMessage {
    override val shortMessage: String = "NEW MESSAGE"
  }

  case class SystemMessage(userId: String, body: String)
}
