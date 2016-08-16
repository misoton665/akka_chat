package com.example.routes

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.example.chat.ChatSystemService
import scalikejdbc._

case class UsersRoute()(implicit dBSession: DBSession) extends RouteExtractable {
  val chatSystem = ChatSystemService()

  override def route: Route =
    path("signUp") {
      (get & parameter('userId, 'name)) { (userId, name) =>
        complete {
          chatSystem.signUp(userId, name) match {
            case Some(_) => s"signUp by $userId, $name"
            case None => s"$userId was already signed up"
          }
        }
      }
    }
}
