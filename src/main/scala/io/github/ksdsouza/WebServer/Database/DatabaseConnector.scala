package io.github.ksdsouza.WebServer.Database

import java.util

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.twitter.util.Future
import io.github.ksdsouza.WebServer.Util.FutureConverter
import io.github.ksdsouza.WebServer.Validation.Anime
import org.mongodb.scala.{Completed, Document, MongoClient, MongoDatabase, SingleObservable, _}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global

object DatabaseConnector {

    def getCollection(season:String, year: Int): Future[List[JsonNode]] = {
        FutureConverter.scalaToTwitterFuture({
          database.getCollection(s"$season $year")
            .find
            .toFuture
            .map(docSeq =>
              docSeq.foldRight(new util.ArrayList[JsonNode]())((document, l) => {
                def docToJsonNode(document: Document): JsonNode = mapper.readTree(document.toJson)
                l.add(docToJsonNode(document))
                l
              }))
            .map(_.asScala.toList)
        })
    }

  val mapper = new ObjectMapper().registerModule(DefaultScalaModule)

  val databaseClient = MongoClient("mongodb://localhost:27017")
  val database: MongoDatabase = databaseClient.getDatabase("Anime")

  def addToDB(season: String, year: Int, anime: Anime): SingleObservable[Completed] =
    database.getCollection(s"$season $year")
            .insertOne(Document.apply(anime.json.toString))

  def addToDB(season: String, year: Int, animeList: List[Anime]) = {

    val collection = database.getCollection(s"$season $year")
    collection.insertMany(animeList.map(anime => Document.apply(anime.json.toString))).toFuture
//    database.listCollectionNames().foreach(println)
//    val collections = database.listCollectionNames()
//    collections.filter(name => name.equals(s"$season $year"))
//    collections.foreach(a => println(s"@$a"))
//    collections.foreach(c =>
//    if(c.isEmpty) {
//      println("!!!")
//      database.createCollection(s"$season $year")
//    })
//    database.getCollection(s"$season $year").insertMany(animeList.map(anime => {
////      println(System.currentTimeMillis)
//      Document.apply(anime.json.toString)
//    }))
  }

  def disconnect(): Unit = databaseClient.close()
}

