package io.github.ksdsouza.WebServer

import java.io.FileInputStream
import java.util.Properties

trait ServerProperties{
  val ServerURL: String
  val ServerPort: Int
}

trait MongoProperties{
  val MongoURL: String
  val MongoPort: Int
  val DBName: String
}

object PropertyReader extends Properties with ServerProperties with MongoProperties {
  load(new FileInputStream("etc/environment.properties"))
  override val ServerURL: String = getProperty("server.url")
  override val ServerPort: Int = getProperty("server.port").toInt

  override val MongoURL: String = getProperty("mongo.url")
  override val MongoPort: Int = getProperty("mongo.port").toInt
  override val DBName: String = getProperty("mongo.dbName")
}
