package io.github.ksdsouza.WebServer

import io.github.ksdsouza.WebServer.Database.DatabaseConnector
import io.github.ksdsouza.WebServer.Routing.{GetEndpoint, PostEndpoint, PutEndpoint}

object TohruAPI {
//  val service = (GetEndpoint.getAnimeSeason :+: PutEndpoint.putEntireSeason :+: PutEndpoint.put2).toService
  val service = (GetEndpoint.getAnimeSeason :+: PutEndpoint.putEntireSeason :+: PostEndpoint.updateGivenAnime).toService
  def exit = {
    DatabaseConnector.disconnect
  }
}
