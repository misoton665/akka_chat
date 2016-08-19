package com.example

import akka.actor.{ActorSystem, Props}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.example.chat.{ChatGroupActor, ChatSystemService}
import com.example.dbmodels.{ChatMessageDBGatewayImpl, ChatUserDBGatewayImpl}
import com.example.routes.{ChatRoute, UsersRoute}
import com.typesafe.config.ConfigFactory

import scala.io.StdIn

object Hello extends App {

  import scalikejdbc._
  import skinny.orm._, feature._

  skinny.DBSettings.initialize()
  implicit val session = AutoSession

  implicit val system = ActorSystem("akka-chat")
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  implicit val chatUserDBGatewayImpl = ChatUserDBGatewayImpl()
  implicit val chatMessageDBGatewayImpl = ChatMessageDBGatewayImpl()
  implicit val chatSystemService = ChatSystemService(system.actorOf(Props[ChatGroupActor]))

  val logger = Logging(system, getClass)

  val config = ConfigFactory.load()

  val route =
    pathEndOrSingleSlash {
      get {
        complete(HttpResponse(entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>akka-chat</h1>")))
      }
    } ~
      pathPrefix("users") {
        UsersRoute().route
      } ~
      pathPrefix("chat") {
        ChatRoute().route
      }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  bindingFuture.onFailure {
    case err: Exception => logger.error(err, "binding failure!")
  }

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}
