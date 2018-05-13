package io.github.ksdsouza.WebServer.Util

import java.io.FileInputStream
import java.util.Properties

trait ServerProperties{
  val ServiceURL: String
  val ServicePort: Int
}

trait MongoProperties{
  val MongoURL: String
  val MongoPort: Int
  val DBName: String
}

trait ZookeeperProperties{
  val ZKURL: String
  val ZKPort: Int
}

object PropertyReader extends Properties with ServerProperties with MongoProperties with ZookeeperProperties {
  load(new FileInputStream("etc/environment.properties"))
  override val ServiceURL: String = getProperty("server.url")
  override val ServicePort: Int = getProperty("server.port").toInt

  override val MongoURL: String = getProperty("mongo.url")
  override val MongoPort: Int = getProperty("mongo.port").toInt
  override val DBName: String = getProperty("mongo.dbName")

  override val ZKURL: String = getProperty("zookeeper.url")
  override val ZKPort: Int = getProperty("zookeeper.port").toInt
}
