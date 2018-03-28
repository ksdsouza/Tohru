package io.github.ksdsouza.WebServer.Database

import io.github.ksdsouza.WebServer.PropertyReader
import org.mongodb.scala.MongoClient

class DatabaseConnector {
  val databaseClient = MongoClient(s"mongodb://${PropertyReader.MongoURL}:${PropertyReader.MongoPort}")
  val database = databaseClient.getDatabase(PropertyReader.DBName)

  def getCollection(season: String, year: Int) = database.getCollection(s"$season $year")
}
