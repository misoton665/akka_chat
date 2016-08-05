package com.example.routes

import akka.http.scaladsl.server.Route

trait RouteExtractable {
  def route: Route
}
