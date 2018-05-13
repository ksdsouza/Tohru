package io.github.ksdsouza.WebServer

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import io.github.ksdsouza.WebServer.Database.DatabaseConnector
import io.github.ksdsouza.WebServer.Routing.{GetEndpoint, PostEndpoint, PutEndpoint}

object TohruAPI {
  val service: Service[Request, Response] =
    (GetEndpoint.endpoint :+: PutEndpoint.putEntireSeason :+: PostEndpoint.updateGivenAnime).toService

  def exit(): Unit = {
    DatabaseConnector.disconnect()
  }
}
