package io.github.ksdsouza.WebServer.Routing




import com.twitter.finagle.http.{Response, Status}
import io.circe.generic.auto._
import io.finch._
import io.finch.circe._
import io.finch.syntax._
import io.github.ksdsouza.WebServer.Database.DatabaseConnector
import io.github.ksdsouza.WebServer.Validation.{Anime, POSTValidatedPayload, PUTValidatedPayload}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object PostEndpoint {
  val updateGivenAnime: Endpoint[Response] = post("services" :: "tohru" :: "update" :: jsonBody[POSTValidatedPayload]) {
    (animeList: POSTValidatedPayload) => {
      Future.apply(animeList).foreach(anime => {
        val season = anime.season
        val year = anime.year
        anime.anime.foreach(a => DatabaseConnector.updateItem(season, year, a))
      })
      Response.apply(Status.Ok)
    }
  }
}
