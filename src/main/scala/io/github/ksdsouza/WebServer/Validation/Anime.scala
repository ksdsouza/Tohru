package io.github.ksdsouza.WebServer.Validation

import java.util

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}

import scala.collection.JavaConverters._


class Anime private(val title: String,
                    numEps: Option[String] = None,
                    imgUrl: Option[String] = None,
                    releaseDate: Option[String] = None,
                    synopsis: Option[String] = None,
                    genres: Option[List[String]] = None) {

  private def this(title: String, numEps: String, imgUrl: String, releaseDate: String, synopsis: String, genres: List[String]) =
    this(title, Some(numEps), Some(imgUrl), Some(releaseDate), Some(synopsis), Some(genres))

  private def this(pAnime: PUTAnime) = this(pAnime.title, pAnime.numEps, pAnime.imgUrl, pAnime.release, pAnime.synopsis, pAnime.genres.toList)

  private def this(pAnime: POSTAnime) = this(pAnime.title, pAnime.numEps, pAnime.imgUrl, pAnime.release, pAnime.synopsis, pAnime.genres.map(_.toList))

  val json = new ObjectMapper().createObjectNode()
  json.put("title", title)

  if (numEps.isDefined) json.put("numEps", numEps.get)
  if (imgUrl.isDefined) json.put("imgUrl", imgUrl.get)
  if (releaseDate.isDefined) json.put("releaseDate", releaseDate.get)
  if (synopsis.isDefined) json.put("synopsis", synopsis.get)
  if (genres.isDefined) {
    val genresJson = json.putArray("genres")
    genres.get.foreach(g => genresJson.add(g))
  }
}

object Anime {

  private def checkFields(animeNode: JsonNode, fields: List[String]): Option[String] =
    if (fields.isEmpty) None
    else if (animeNode.has(fields.head)) checkFields(animeNode, fields.tail)
    else Some(s"Anime requires field: ${fields.head}")

  def apply(pAnime: PUTAnime) = new Anime(pAnime)

  def apply(pAnime: POSTAnime) = new Anime(pAnime)

  def apply(animeNode: JsonNode, requireAll: Boolean = true): Either[String, Anime] = {
    val allFieldsPresent = checkFields(animeNode, List("title"))
    if (allFieldsPresent.isDefined) return Left(allFieldsPresent.get)

    if (requireAll) {
      val allFieldsPresent = checkFields(animeNode, List("numEps", "imgUrl", "release", "synopsis", "genres"))

      if (allFieldsPresent.isDefined) Left(allFieldsPresent.get)
      else if (!animeNode.get("genres").isArray) new Left(s"Genres field must be an array for ${animeNode.get("title")}")
      else {
        val genresList = new util.ArrayList[String]
        animeNode.get("genres").forEach(genre => genresList.add(genre.asText))

        new Right(new Anime(
          animeNode.get("title").asText,
          animeNode.get("numEps").asText,
          animeNode.get("imgUrl").asText,
          animeNode.get("release").asText,
          animeNode.get("synopsis").asText,
          animeNode.get("genres").asScala.toList.map(_.asText)
        ))
      }
    }
    else if (animeNode.has("genres") && !animeNode.get("genres").isArray)
      new Left(s"Genres field must be an array for ${animeNode.get("title")}")
    else {
      val title = animeNode.get("title").asText
      val numEps = if (animeNode.has("numEps")) Some(animeNode.get("numEps").asText) else None
      val imgUrl = if (animeNode.has("imgUrl")) Some(animeNode.get("imgUrl").asText) else None
      val release = if (animeNode.has("release")) Some(animeNode.get("release").asText) else None
      val synopsis = if (animeNode.has("synopsis")) Some(animeNode.get("synopsis").asText) else None
      val genres = if (animeNode.has("genres")) Some(animeNode.get("genres").asScala.toList.map(_.asText)) else None
      new Right(new Anime(title, numEps, imgUrl, release, synopsis, genres))
    }
  }
}
