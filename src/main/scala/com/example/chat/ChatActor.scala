package com.example.chat

import akka.actor.{Actor, Props}

class ChatActor extends Actor {

  override def preStart(): Unit = {
    super.preStart()
    context.actorOf(Props[MessageBroadcastActor], "broadcaster")
  }

  override def receive: Receive = ???
}
