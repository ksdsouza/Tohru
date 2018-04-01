package io.github.ksdsouza.WebServer.Routing

import com.twitter.finagle.http.Response
import com.twitter.finagle.{Service, http}

trait Router extends Service[http.Request, http.Response]{
  def getResponse(contentString: String, status: http.Status): Response = {
    val response = http.Response(status)
    response.setContentString(contentString)
    response
  }
}
