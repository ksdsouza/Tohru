package io.github.ksdsouza.WebServer.Routing

import com.twitter.finagle.http.Response
import com.twitter.finagle.{Service, http}

trait Router extends Service[http.Request, http.Response]{

  implicit class ExtendedResponse(val response: http.Response) {
    def withContentString(contentString:String) = {
      response.setContentString(contentString)
      response
    }
  }

  def getResponse(contentString: String, status: http.Status): Response = http.Response(status).withContentString(contentString)
}
