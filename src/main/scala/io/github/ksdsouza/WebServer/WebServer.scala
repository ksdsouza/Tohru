package io.github.ksdsouza.WebServer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import org.mongodb.scala.MongoClient

import scala.io.StdIn

object MainRouter {
  val routes = GetRouter.route
}

object WebServer {
  def main(args: Array[String]) {
    implicit val system = ActorSystem("my-system")

    def onComplete(): Unit = {
      client.close()
      system.terminate()
    }

    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val bindingFuture = Http().bindAndHandle(MainRouter.routes, PropertyReader.ServerURL, PropertyReader.ServerPort)

    println(s"Server online at http://${PropertyReader.ServerURL}:${PropertyReader.ServerPort}/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => onComplete()) // and shutdown when done
  }
}