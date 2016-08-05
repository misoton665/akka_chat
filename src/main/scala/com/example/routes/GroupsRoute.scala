package com.example.routes
import akka.http.scaladsl.server.Route

object GroupsRoute extends RouteExtractable{

  import akka.http.scaladsl.server.Directives._

  override def route: Route =
    path("search") {
      (get & parameter('name)) { groupName =>
        complete {
          s"search by $groupName"
        }
      }
    }
}
