package io.github.ksdsouza.WebServer.Database

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.twitter.bijection.Conversion
import com.twitter.util.{Future => TwitterFuture}
import io.github.ksdsouza.WebServer.Util.FutureConverter
import io.github.ksdsouza.WebServer.Validation.{Anime, POSTAnime}
import org.bson.conversions.Bson
import org.mongodb.scala.{Completed, Document, MongoClient, MongoDatabase, SingleObservable, _}
import org.mongodb.scala.model.Updates._
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global

object DatabaseConnector {
  val mapper = new ObjectMapper()

  val databaseClient = MongoClient("mongodb://localhost:27017")
  val database: MongoDatabase = databaseClient.getDatabase("Anime")

  def getCollection(season: String, year: Int): TwitterFuture[List[JsonNode]] = {
    def docToJsonNode(document: Document): JsonNode = mapper.readTree(document.toJson)

    FutureConverter.scalaToTwitterFuture(
      database.getCollection(s"$season $year")
        .find.toFuture
        .map(docSeq =>
          docSeq.foldRight(new ArrayBuffer[JsonNode])((document, l) => l += docToJsonNode(document)))
        .map(_.toList)
    )
  }

  def addToDB(season: String, year: Int, anime: Anime) =
    database.getCollection(s"$season $year")
      .insertOne(Document.apply(anime.json.toString)).toFuture

  def addToDB(season: String, year: Int, animeList: List[Anime]) = {
    val collection = database.getCollection(s"$season $year")
    animeList.foreach(anime => {
      val query = Document.apply("{\"title\":\"" + anime.title + "\"}")
      val itemInDB = collection.find(query).toFuture()
      itemInDB.map(docs => {
        docs.foreach(collection.deleteOne)
      }).onComplete(_ => {
        println("Adding docs")
        collection.insertOne(Document.apply(anime.json.toString))
        //Thread.sleep(300)
      })
    })
  }

  def updateItem(season: String, year: Int, anime: POSTAnime): Unit = {
    val collection = database.getCollection(s"$season $year")
    val query = Document.apply("{\"title\":\"" + anime.title + "\"}")
//    val query = Document.apply("{\"numEps\":\"13\"}")
    println(query.toJson)
    println(s"Going to update: ${anime.title}")
    collection.updateMany(query, anime.getAllUpdates).toFuture
      .onComplete(result => println(result.get))
  }

  def disconnect(): Unit = databaseClient.close()
}

