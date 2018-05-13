package io.github.ksdsouza.WebServer.Database

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.twitter.bijection.Conversion
import com.twitter.util.{Future => TwitterFuture}
import io.github.ksdsouza.WebServer.Util.{FutureConverter, PropertyReader}
import io.github.ksdsouza.WebServer.Validation.{Anime, POSTAnime}
import org.bson.conversions.Bson
import org.mongodb.scala.model.UpdateOptions
import org.mongodb.scala.{Completed, Document, MongoClient, MongoDatabase, _}
import org.mongodb.scala.model.Updates._


import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Failure
import scala.util.{Try,Success,Failure}

object DatabaseConnector {
  val mapper = new ObjectMapper()

  val databaseClient: MongoClient = MongoClient(s"mongodb://${PropertyReader.MongoURL}:${PropertyReader.MongoPort}")
  val database: MongoDatabase = databaseClient.getDatabase(PropertyReader.DBName)

  def getCollection(season: String, year: Int): TwitterFuture[List[JsonNode]] = {
    def docToJsonNode(document: Document): JsonNode = mapper.readTree(document.toJson)

    FutureConverter.scalaToTwitterFuture(
      database.getCollection(s"$season $year")
        .find.toFuture
        .map(_.toList)
        .map(docList => docList.map(docToJsonNode))
    )
  }

  def getCollectionNames(): TwitterFuture[Seq[String]] = FutureConverter.scalaToTwitterFuture(database.listCollectionNames().toFuture())


  def hasCollection(collectionName: String): Future[Boolean] = {
    database.listCollectionNames().toFuture().map(collectionNames => collectionNames.exists(name => name.equals(collectionName)))
  }

  def createCollection(collectionName: String): TwitterFuture[Completed] = {
    FutureConverter.scalaToTwitterFuture(database.createCollection(collectionName).toFuture)
  }

  def addItemToCollection(anime: Anime, collection: MongoCollection[Document]): Unit = {
    val query = Document.apply("{\"title\":\"" + anime.title + "\"}")
    val doc = Document.apply(anime.json.toString)

    collection.find(model.Filters.eq("title", anime.title)).toFuture().onComplete {
      case Success(s) if s.nonEmpty=> {
        println(s)
        collection.replaceOne(query, doc).toFuture()
      }

      case Success(s) => {
        collection.insertOne(doc).toFuture()
      }
      case Failure(p) => {
        println(p)
      }
    }
//    FutureConverter.scalaToTwitterFuture(collection.insertOne(Document.apply(anime.json.toString)).toFuture())
//      .onSuccess(_ => println("Successfully added " + anime.title + " to collection"))
  }

  def addToDB(season: String, year: Int)(animeList: List[Anime]): Unit = {
    val collectionName = s"$season $year"
    hasCollection(collectionName).map(
      collectionExists => {
        if(collectionExists){
          val collection = database.getCollection(s"$season $year")
          animeList.foreach(anime => addItemToCollection(anime, collection))
        }
        else {
          createCollection(collectionName).onSuccess(_ => addToDB(season, year)(animeList))
        }
      }
    )
  }



  def updateItem(season: String, year: Int, anime: POSTAnime): Unit = {
    val collection = database.getCollection(s"$season $year")
    val query = Document.apply("{\"title\":\"" + anime.title + "\"}")
    println(s"Going to update: ${anime.title}")
    collection
      .updateMany(query, anime.getAllUpdates)
      .toFuture
      .onComplete(result => println(result.get))
  }

  def disconnect(): Unit = databaseClient.close
}

