package io.github.ksdsouza.WebServer.Routing

import com.twitter.finagle.http.{Response, Status}
import io.circe.generic.auto._
import io.finch._
import io.finch.circe._
import io.finch.syntax._
import io.github.ksdsouza.WebServer.Database.DatabaseConnector
import io.github.ksdsouza.WebServer.Validation.{Anime, PUTAnime, PUTValidatedPayload}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object PutEndpoint {
  val putEntireSeason:Endpoint[Response] = put("services" :: "tohru" :: "insert" :: jsonBody[PUTValidatedPayload]) {
    (payload: PUTValidatedPayload) => {
      println("Starting put endpoint")
      val addToDB: List[Anime] => Unit = DatabaseConnector.addToDB(payload.season, payload.year)
      Future.apply(payload)
        .map(_.asList)
        .map(addToDB)
        .onComplete(_ => println(s"Completed inserting payload for ${payload.season} ${payload.year}"))
      Response.apply(Status.Ok)
    }
  }
}
