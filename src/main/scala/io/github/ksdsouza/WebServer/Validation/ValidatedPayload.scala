package io.github.ksdsouza.WebServer.Validation

import com.fasterxml.jackson.databind.ObjectMapper
import org.bson.Document
import org.bson.conversions.Bson
import org.mongodb.scala.bson
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.bson.conversions
import org.mongodb.scala.model.Updates._

import scala.collection.mutable.ArrayBuffer

case class PUTAnime(title: String, numEps: String, imgUrl: String, release: String, synopsis: String,
                    genres: Array[String]) {
  def asAnime = Anime.apply(this)
}

case class POSTAnime(title: String, numEps: Option[String], imgUrl: Option[String], release: Option[String],
                     synopsis: Option[String], genres: Option[Array[String]]){
  def asAnime = Anime.apply(this)

  private def fieldToUpdateSet(key: String, field: Option[Any]) = field.getOrElse(None) match {
    case textField: String => set(key, textField)
    case arrayField: Array[String] if arrayField.length > 0 =>
      arrayField.foldRight(push(key,arrayField.head))((current, accumulator) => combine(accumulator, push(key, current)))
    case _ => null
  }

  def getAllUpdates: Bson = {
    val titleSet = fieldToUpdateSet("title", Some(title))
    val numEpsSet = fieldToUpdateSet("numEps", numEps)
    val imgUrlSet = fieldToUpdateSet("imgUrl", imgUrl)
    val releaseSet = fieldToUpdateSet("release", release)
    val synopsisSet = fieldToUpdateSet("synopsis", synopsis)
    val genresSet = fieldToUpdateSet("genres", genres)

    val updates = new ArrayBuffer[Bson]()

    if(numEpsSet != null) updates += numEpsSet
    if(imgUrlSet != null) updates += imgUrlSet
    if(releaseSet != null) updates += releaseSet
    if(synopsisSet != null) updates += synopsisSet
    if(genresSet != null) {
      val arrayNode = new ObjectMapper().createArrayNode()
      genres.get.foreach(arrayNode.add)
      updates += set("genres", arrayNode.toString)
    }

    val updateBSON = updates.foldRight(titleSet)((current, accumulation) => combine(accumulation, current))
    println(updateBSON)
    updateBSON
  }
}

case class PUTValidatedPayload(season: String, year: Int, anime: Array[PUTAnime]) {
  def asList: List[Anime] = anime.toList.map(Anime.apply)
}

case class POSTValidatedPayload(season: String, year: Int, anime: Array[POSTAnime]) {
  def asList: List[POSTAnime] = anime.toList
}