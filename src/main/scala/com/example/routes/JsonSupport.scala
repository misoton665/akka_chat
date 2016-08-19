package com.example.routes

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol{
  implicit val simpleChatUser = jsonFormat2(SimpleChatUser.apply)
}
