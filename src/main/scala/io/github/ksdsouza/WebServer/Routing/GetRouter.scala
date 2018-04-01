package io.github.ksdsouza.WebServer.Routing

import java.util

import com.twitter.finagle.http
import com.twitter.finagle.http.Response
import com.twitter.util.Future
import io.github.ksdsouza.WebServer.Database.DatabaseConnector

object GetRouter extends Router {
  override def apply(request: http.Request): Future[Response] = {
    if (!request.getParamNames().containsAll(util.Arrays.asList("season", "year")))
      Future.value(getResponse("Get Request expects both a 'season' and 'year' parameter", http.Status.BadRequest))
    else{
      val season = request.getParam("season")
      val year = request.getIntParam("year")
      val collectionJSON = new DatabaseConnector().getCollection(season, year)
      collectionJSON.map(jsonNode => getResponse(jsonNode.toString, http.Status.Ok))
    }
  }
}
