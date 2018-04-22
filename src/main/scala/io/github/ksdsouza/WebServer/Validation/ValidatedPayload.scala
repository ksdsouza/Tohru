package io.github.ksdsouza.WebServer.Validation

import com.fasterxml.jackson.databind.ObjectMapper
import org.bson.Document
import org.bson.conversions.Bson
import org.mongodb.scala.bson
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.bson.conversions
import org.mongodb.scala.model.Updates._

import scala.collection.mutable.ArrayBuffer

case class PUTAnime(title: String, numEps: String, imgUrl: String, release: String, synopsis: String, genres: Array[String]) {
  def asAnime = Anime.apply(this)
}

case class POSTAnime(title: String, numEps: Option[String], imgUrl: Option[String], release: Option[String], synopsis: Option[String], genres: Option[Array[String]]){
  def asAnime = Anime.apply(this)

  private def otherToSet(key: String, field: Option[String]) = if (field.isDefined) set(key, field.get) else null
  private def listToSet(key: String, field: Option[Array[String]]) =
    if (field.isDefined && field.get.size > 0) {
      val f = field.get
      f.foldRight(push(key,f.head))((current, accumulator) => combine(accumulator, push(key, current)))
    }
    else null

  def titleAsUpdate = set("title", title)

  def numEpsAsUpdate = otherToSet("numEps", numEps)

  def imgUrlAsUpdate = otherToSet("imgUrl", imgUrl)

  def releaseAsUpdate = otherToSet("release", release)

  def synopsisAsUpdate = otherToSet("synopsis", synopsis)

  def genresAsUpdate = listToSet("genres", genres)

  def getAllUpdates = {
    val titleSet = titleAsUpdate
    val numEpsSet = numEpsAsUpdate
    val imgUrlSet = imgUrlAsUpdate
    val releaseSet = releaseAsUpdate
    val synopsisSet = synopsisAsUpdate
    val genresSet = genresAsUpdate

    val updates = new ArrayBuffer[Bson]()

    if(numEpsSet != null) updates += numEpsSet
    if(imgUrlSet != null) updates += imgUrlSet
    if(releaseSet != null) updates += releaseSet
    if(synopsisSet != null) updates += synopsisSet
    if(genresSet != null) {
      val arrayNode = new ObjectMapper().createArrayNode()
      genres.get.foreach(arrayNode.add)
      updates += set("genres", arrayNode.toString)
//      updates += set("genres", genres.get)
    }

    val r = updates.foldRight(titleSet)((current, accumulation) => combine(accumulation, current))
    println(r)
    r
  }
}

case class PUTValidatedPayload(val season: String, val year: Int, val anime: Array[PUTAnime]) {
  def asList = anime.toList
}

case class POSTValidatedPayload(season: String, year: Int, anime: Array[POSTAnime]) {
  def asList = anime.toList
}