package io.github.ksdsouza.WebServer

import com.twitter.finagle.http.Method
import com.twitter.finagle.{Service, http}
import com.twitter.util.Future
import io.github.ksdsouza.WebServer.Routing.{GetRouter, PostRouter, PutRouter}

object TohruAPI extends Service[http.Request, http.Response]{
  def apply(request: http.Request): Future[http.Response] = {
    request.method match {
      case Method.Get => GetRouter.apply(request)
      case Method.Post => PostRouter.apply(request)
      case Method.Put => PutRouter.apply(request)
      case _ => Future.value({
        val response = http.Response.apply(http.Status.BadGateway)
        response.setContentString(s"HTTP Method: ${request.method.name} is unknown")
        response
      })
    }
  }
}
