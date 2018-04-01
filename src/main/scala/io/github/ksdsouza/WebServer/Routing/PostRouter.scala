package io.github.ksdsouza.WebServer.Routing

import com.twitter.finagle.http
import com.twitter.util.Future
import io.github.ksdsouza.WebServer.Validation.ValidatedPayload

object PostRouter extends Router {
  override def apply(request: http.Request): Future[http.Response] = {
    val payloadEither = ValidatedPayload.apply(request)
    if(payloadEither.isLeft) Future.value(getResponse(payloadEither.left.get, http.Status.BadRequest))
    else {
      Future.value({
        val payload = payloadEither.right.get
      })
      Future.value(http.Response(http.Status.Ok))
    }}
}
