package io.github.ksdsouza.WebServer.Routing

import com.fasterxml.jackson.databind.ObjectMapper
import com.twitter.finagle.http
import com.twitter.finagle.http.Response
import io.finch._
import io.finch.syntax._
import io.github.ksdsouza.WebServer.Database.DatabaseConnector

object GetEndpoint {
  def getResponse(contentString: String, status: http.Status) = {
    val response = http.Response.apply(status)
    response.setContentString(contentString)
    response
  }

  val getAnimeSeason: Endpoint[Response] = get("services" :: "tohru" :: "fetch" :: param("season") :: param[Int]("year")) {
    (season: String, year: Int) => {
      DatabaseConnector.getCollection(season, year)
        .map(_.foldRight(new ObjectMapper().createArrayNode)((doc, curr) => curr.add(doc)))
        .map(_.toString).map(getResponse(_, http.Status.Ok))
    }
  }

}
