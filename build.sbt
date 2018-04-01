name := "tohru"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.2.1",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.0",
  "com.twitter" %% "finagle-http" % "18.3.0"
)