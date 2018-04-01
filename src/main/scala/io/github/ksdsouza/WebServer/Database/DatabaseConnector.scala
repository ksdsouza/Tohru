package io.github.ksdsouza.WebServer.Database

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.twitter.util.{Future => TwitterFuture}
import io.github.ksdsouza.WebServer.Util.{FutureConverter, PropertyReader}
import org.mongodb.scala.{Document, MongoClient}

import scala.util.parsing.json.JSONType
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DatabaseConnector {

  val databaseClient = MongoClient(s"mongodb://${PropertyReader.MongoURL}:${PropertyReader.MongoPort}")
  val database = databaseClient.getDatabase(PropertyReader.DBName)

  def getCollection(season: String, year: Int): TwitterFuture[ArrayNode] = {
    FutureConverter.scalaToTwitterFuture({
      val mapper = new ObjectMapper
      val node = mapper.createArrayNode
      val allItems: Future[Seq[Document]] = database.getCollection(s"$season $year").find.toFuture

      allItems.map(_.map(document => node.add(mapper.readTree(document.toJson))).head)
    })
  }

  def addToDB(season: String, year: Int, document: JSONType) = {
    val collection = database.getCollection(s"$season $year")
    //collection.insertOne(Document.apply(document.toString()))
  }

  def disconnect(): Unit = databaseClient.close()
}

