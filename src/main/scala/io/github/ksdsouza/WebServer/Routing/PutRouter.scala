package io.github.ksdsouza.WebServer.Routing

import com.twitter.finagle.http
import com.twitter.util._
import io.github.ksdsouza.WebServer.Database.DatabaseConnector
import io.github.ksdsouza.WebServer.Validation.ValidatedPayload
import scala.concurrent.ExecutionContext.Implicits.global
object PutRouter extends Router {
  override def apply(request: http.Request): Future[http.Response] = {
    val payloadEither = ValidatedPayload.apply(request)
    if(payloadEither.isLeft) Future.value(getResponse(payloadEither.left.get, http.Status.BadRequest))
    else {
      FuturePool.unboundedPool.apply({
        val payload = payloadEither.right.get
        DatabaseConnector.addToDB(payload.season, payload.year, payload.anime)
      }).map(_.map(completion => println("PutRouter: " + completion.toString)))
      Future.value(http.Response(http.Status.Ok))
    }
  }
}
