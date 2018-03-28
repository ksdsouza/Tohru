package io.github.ksdsouza.WebServer

import akka.http.scaladsl.server.Directives._
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.ksdsouza.WebServer.Database.DatabaseConnector

object GetRouter {
  val route =
    parameters('season,'year){ (season, year) =>
      get{
        println(s"$season $year")
        val collection = new DatabaseConnector().getCollection(season, year.toInt)

        onSuccess(collection.find.collect.head){ documentResult =>
          val objectMapper = new ObjectMapper
          val r = objectMapper.createArrayNode
          documentResult.foreach(singleDoc => r.add(objectMapper.readTree(singleDoc.toJson())))
          complete(r.toString)
        }
      }
    }
}
