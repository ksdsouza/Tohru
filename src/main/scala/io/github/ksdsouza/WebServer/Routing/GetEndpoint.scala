package io.github.ksdsouza.WebServer.Routing

import com.fasterxml.jackson.databind.ObjectMapper
import com.twitter.finagle.http
import com.twitter.finagle.http.Response
import io.finch._
import io.finch.syntax._
import io.github.ksdsouza.WebServer.Database.DatabaseConnector

object GetEndpoint {
  def endpoint = getAnimeSeason :+: getSeasons

  def setResponse(contentString: String, status: http.Status): Response = {
    val response = http.Response.apply(status)
    response.setContentString(contentString)
    response
  }

  val getAnimeSeason: Endpoint[Response] = get("services" :: "tohru" :: "fetch" :: param("season") :: param[Int]("year")) {
    (season: String, year: Int) => {
      DatabaseConnector.getCollection(season, year)
        .map(fetched => fetched.foldRight(new ObjectMapper().createArrayNode)((doc, jsonArray) => jsonArray.add(doc)))
        .map(_.toString)
        .map(setResponse(_, http.Status.Ok))
    }
  }

  val getSeasons: Endpoint[Response] = get("services" :: "tohru" :: "list") {
    () => {
      DatabaseConnector.getCollectionNames().map(names => names.foldRight(new ObjectMapper().createArrayNode()) {
        (currentName, responseJSON) => responseJSON.add(currentName)
      }).map(responseJson => setResponse(responseJson.toString, http.Status.Ok))
    }
  }
}
