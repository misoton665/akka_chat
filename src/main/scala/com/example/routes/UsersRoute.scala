package com.example.routes

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.example.dbmodels.ChatUser
import scalikejdbc._

case class UsersRoute()(implicit dBSession: DBSession) extends RouteExtractable {
  override def route: Route =
    path("signUp") {
      (get & parameter('userId, 'name)) { (userId, name) =>
        complete {
          if (ChatUser.where('userId -> userId).count('userId) == 0) {
            ChatUser.createWithAttributes('userId -> userId, 'name -> name)
            s"signUp by $userId, $name"
          } else {
            s"$userId was already signed up"
          }
        }
      }
    }
}
