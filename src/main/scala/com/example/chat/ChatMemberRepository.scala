package com.example.chat

import akka.actor.ActorRef

object ChatMemberRepository {

  type Participant = ActorRef
  private var participants = Map.empty[String, Participant]

  def insertParticipant(userId: String, userActor: Participant): Unit = {
    participants += userId -> userActor
  }

  def deleteParticipant(userId: String): Unit = {
    participants -= userId
  }

  def findParticipant(userId: String): Option[Participant] = {
    participants.get(userId)
  }

  def findParticipant(p: ((String, Participant)) => Boolean): Map[String, Participant] = {
    participants.filter(p)
  }
}
