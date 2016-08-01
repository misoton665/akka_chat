package com.example

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.example.models.{ChatUser, ChatUser, Message, Message}
import com.typesafe.config.ConfigFactory

import scala.io.StdIn

object Hello extends App {

  import scalikejdbc._
  import skinny.orm._, feature._
  import org.joda.time._

  skinny.DBSettings.initialize()
  implicit val session = AutoSession

  implicit val system = ActorSystem("akka-chat")
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  val logger = Logging(system, getClass)

  val config = ConfigFactory.load()

  val route =
    pathSingleSlash {
      get {
        complete(HttpResponse(entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>")))
      }
    } ~
      pathPrefix("echo") {
        (get & path(Segment)) { echoStr =>
          complete {
            echoStr
          }
        }
      } ~
      path("conf") {
        get {
          complete {
            config.getString("app.hoge.puyo")
          }
        }
      } ~
      path("addColmun") {
        get {
          complete {
            sql"create table poe(id serial, poe varchar(30));".execute.apply()
            ""
          }
        }
      } ~
      pathPrefix("user") {
        (get & path(Segment)) { userName =>
          complete {
            val user: List[ChatUser] = ChatUser.findAll()
            user.map(chatUser => chatUser.nickname).mkString(", ")
          }
        }
      } ~
      pathPrefix("message") {
        get {
          complete {
            val user: List[Message] = Message.findAll()
            user.map(message => message.body).mkString(", ")
          }
        }
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
