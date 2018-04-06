package io.github.ksdsouza.WebServer.Validation

import com.fasterxml.jackson.databind.node.JsonNodeType
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.twitter.finagle.http
import com.twitter.finagle.http.{Method, Request}

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

class ValidatedPayload private(val season: String, val year: Int, val anime: List[Anime])

object ValidatedPayload {

  def getSeason(seasonNum: Int): String = seasonNum match {
    case 0 => "fall"
    case 1 => "winter"
    case 2 => "spring"
    case 3 => "summer"
  }

  def apply(request: http.Request): Either[String, ValidatedPayload] = apply(request, request.method)

  def apply(request: http.Request, method: Method): Either[String, ValidatedPayload] = method match {
    case Method.Put => PUTValidation(request)
    case Method.Post => POSTValidation(request)
    case _ => new Left("Invalid method supplied")
  }

  private def PUTValidation(request: http.Request):Either[String, ValidatedPayload] = validate(request, true)

  private def POSTValidation(request: Request): Either[String, ValidatedPayload] = validate(request, false)

  private def validate(request: http.Request, requireAllAnimeFields: Boolean = true): Either[String, ValidatedPayload] = {

    def getField[A, B](requestTry: Try[A], right: A => B, predicate: A => Boolean, failure: B): B = requestTry match {
      case Success(i) => if (predicate(i)) right(i) else failure
      case Failure(e) => failure
    }

    def getSeason(requestJson: JsonNode): String = getField(Try(requestJson.get("season").asInt), ValidatedPayload.getSeason(_),
      (i: Int) => (0 <= i && i <= 3), "")

    def getYear(requestJson: JsonNode): Int = getField(Try(requestJson.get("year").asInt), (i: Int) => i, (i: Int) => i >= 0, -1)

    def getAnime(requestJSON: JsonNode): JsonNode = getField(Try(requestJSON.get("anime")), (i: JsonNode) => i,
      (animeJSON: JsonNode) => animeJSON != null && animeJSON.getNodeType == JsonNodeType.ARRAY,  null)

    val requestJSON = Try(new ObjectMapper().readTree(request.getContentString)).getOrElse(null)
    if(requestJSON == null) return Left("Request payload is not valid JSON")

    val season = getSeason(requestJSON)
    if(season.isEmpty) return Left("Payload must include key season with Int value between 0 and 3")

    val year = getYear(requestJSON)
    if (year == -1) return Left("Payload must include key year with positive Int value")

    val animeJson = getAnime(requestJSON)
    if(animeJson == null) return Left("Payload must include key anime with Array of Anime as value")

    val animeList = populateList(new ListBuffer[Anime], animeJson.elements.asScala.toList, requireAllAnimeFields)
    if(animeList.isLeft) new Left(animeList.left.get)
    else new Right(new ValidatedPayload(season, year, animeList.right.get))
  }

  private def populateList(list: ListBuffer[Anime],
                           jsonnodeList: List[JsonNode],
                           requireAll: Boolean = true): Either[String, List[Anime]] =
    if(jsonnodeList.isEmpty) Right(list.toList)
    else Anime.apply(jsonnodeList.head, requireAll).fold(
      left => Left(left),
      right => populateList(list += right, jsonnodeList.tail, requireAll)
    )
}
