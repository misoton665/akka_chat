package com.example.routes

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.example.chat.ChatSystemService

case class UsersRoute()(implicit chatSystemService: ChatSystemService) extends RouteExtractable with JsonSupport {

  override def route: Route =
    path("signUp") {
      (post & entity(as[SimpleChatUser])) { user =>
        complete {
          chatSystemService.signUp(user.userId, user.name) match {
            case Some(_) => s"signUp by ${user.userId}, ${user.name}"
            case None => s"${user.userId} was already signed up"
          }
        }
      }
    }
}
