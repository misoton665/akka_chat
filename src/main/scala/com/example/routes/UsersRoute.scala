package com.example.routes

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.example.chat.ChatSystemService

case class UsersRoute()(implicit chatSystemService: ChatSystemService) extends RouteExtractable {

  override def route: Route =
    path("signUp") {
      (get & parameter('userId, 'name)) { (userId, name) =>
        complete {
          chatSystemService.signUp(userId, name) match {
            case Some(_) => s"signUp by $userId, $name"
            case None => s"$userId was already signed up"
          }
        }
      }
    }
}
