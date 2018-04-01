package io.github.ksdsouza.WebServer

import com.twitter.finagle.http.Method
import com.twitter.finagle.{Service, http}
import com.twitter.util.Future
import io.github.ksdsouza.WebServer.Routing.{GetRouter, PostRouter, PutRouter}

object TohruAPI extends Service[http.Request, http.Response]{

  implicit class ExtendedResponse(val response: http.Response) {
    def withContentString(contentString:String) = {
      response.setContentString(contentString)
      response
    }
  }

  def apply(request: http.Request): Future[http.Response] = request.method match {
      case Method.Get => GetRouter.apply(request)
      case Method.Post => PostRouter.apply(request)
      case Method.Put => PutRouter.apply(request)
      case _ => Future.value(http.Response
          .apply(http.Status.BadGateway)
          .withContentString(s"HTTP Method: ${request.method.name} is unknown"))
  }
}
