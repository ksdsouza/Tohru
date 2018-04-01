package io.github.ksdsouza.WebServer

import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http
import com.twitter.util.{Await, Future}

class WebServer {

  val server = Http.serve(":8085", TohruAPI)
  Await.ready(server)

}

object Main {
  def main(args: Array[String]): Unit = {
    new WebServer
  }
}