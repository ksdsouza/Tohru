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
    else new DatabaseConnector().getCollection(
        request.getParam("season"),
        request.getIntParam("year")
      ).map(jsonNode => getResponse(jsonNode.toString, http.Status.Ok))
  }
}
