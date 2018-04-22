package io.github.ksdsouza.WebServer

import java.net.InetSocketAddress

import com.twitter.app.App
import com.twitter.finagle.Http
import com.twitter.logging.Logging
import com.twitter.server.{Admin, AdminHttpServer, Hooks, Stats}
import com.twitter.util.Await

object RestServer extends App
  with AdminHttpServer
  with Stats
  with Hooks
  with Admin
  with Logging {


  val port = flag[Int]("port",8085, "port this server should use")

  override def failfastOnFlagsNotParsed: Boolean = true


  def main() {

//      val server = Http.server
//          .withLabel("tohru")
//          .serveAndAnnounce(
//            name = "zk!127.0.0.1:2181!/service2/tohru!0",
//            addr = new InetSocketAddress(8085),
//            service = TohruAPI
//          )
    val server2 = Http.server.withLabel("tohru")
        .serveAndAnnounce("zk!127.0.0.1:2181!/service2/tohru!0",
          new InetSocketAddress(8085),
          TohruAPI.service)

    onExit({
      TohruAPI.exit
    })
    Await.ready(server2)
  }

}

