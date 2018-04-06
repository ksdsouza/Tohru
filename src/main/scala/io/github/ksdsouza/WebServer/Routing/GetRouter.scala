package io.github.ksdsouza.WebServer.Routing

import java.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.twitter.finagle.http
import com.twitter.finagle.http.Response
import com.twitter.util.Future
import io.github.ksdsouza.WebServer.Database.DatabaseConnector

object GetRouter extends Router {
  override def apply(request: http.Request): Future[Response] =
    if (!request.getParamNames().containsAll(util.Arrays.asList("season", "year")))
      Future.value(getResponse("Get Request expects both a 'season' and 'year' parameter", http.Status.BadRequest))
    else
      DatabaseConnector.getCollection(request.getParam("season"), request.getIntParam("year"))
        .map(_.foldRight(new ObjectMapper().createArrayNode)((doc, curr) => curr.add(doc)))
        .map(_.toString)
        .map(getResponse(_, http.Status.Ok))

}
