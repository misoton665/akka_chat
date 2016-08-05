package com.example.routes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

object UsersRoute extends RouteExtractable {
  override def route: Route =
    path("signUp") {
      (get & parameter('name, 'nickname)) { (name, nickname) =>
        complete {
          s"signUp by $name, $nickname"
        }
      }
    }
}
