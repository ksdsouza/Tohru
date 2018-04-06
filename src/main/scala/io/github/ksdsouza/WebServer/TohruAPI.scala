package io.github.ksdsouza.WebServer

import com.twitter.finagle.http
import com.twitter.finagle.http.Method
import com.twitter.util.Future
import io.github.ksdsouza.WebServer.Routing.{GetRouter, PostRouter, PutRouter, Router}

object TohruAPI extends Router {
  def apply(request: http.Request): Future[http.Response] = request.method match {
      case Method.Get => GetRouter.apply(request)
      case Method.Post => PostRouter.apply(request)
      case Method.Put => PutRouter.apply(request)
      case _ => Future.value(http.Response
          .apply(http.Status.BadGateway)
          .withContentString(s"HTTP Method: ${request.method.name} is unknown"))
  }
}
