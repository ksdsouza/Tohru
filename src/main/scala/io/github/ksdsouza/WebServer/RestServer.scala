package io.github.ksdsouza.WebServer

import java.net.InetSocketAddress

import com.twitter.app.{App, Flag}
import com.twitter.finagle.Http
import com.twitter.logging.Logging
import com.twitter.server.{Admin, AdminHttpServer, Hooks, Stats}
import com.twitter.util.Await
import io.github.ksdsouza.WebServer.Util.PropertyReader

object RestServer extends App
  with AdminHttpServer
  with Stats
  with Hooks
  with Admin
  with Logging {

  val port: Flag[Int] = flag[Int]("port", PropertyReader.MongoPort, "port this server should use")

  override def failfastOnFlagsNotParsed: Boolean = true


  def main() {

    val server = Http.server.withLabel("tohru")
        .serveAndAnnounce(
          name = s"zk!${PropertyReader.ZKURL}:${PropertyReader.ZKPort}!/services/tohru!0",
          addr = new InetSocketAddress(PropertyReader.ServicePort),
          service = TohruAPI.service)

    onExit({
      println("Stopping Service")
      TohruAPI.exit
    })

    Await.ready(server)
  }
}

