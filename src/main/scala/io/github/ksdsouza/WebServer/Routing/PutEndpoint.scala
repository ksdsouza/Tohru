package io.github.ksdsouza.WebServer.Routing

import com.twitter.finagle.http.{Response, Status}
import io.circe.generic.auto._
import io.finch._
import io.finch.circe._
import io.finch.syntax._
import io.github.ksdsouza.WebServer.Database.DatabaseConnector
import io.github.ksdsouza.WebServer.Validation.{Anime, PUTValidatedPayload}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object PutEndpoint {
  val putEntireSeason:Endpoint[Response] = put("services" :: "tohru" :: "insert" :: jsonBody[PUTValidatedPayload]) {
    (animeArray: PUTValidatedPayload) => {
      Future.apply(animeArray)
        .map(_.asList)
        .map(_.map(Anime.apply))
        .map(animeList => DatabaseConnector.addToDB("winter", 2000, animeList))
      Response.apply(Status.Ok)
    }
  }
}
