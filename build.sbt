name := "tohru"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"   % "10.1.0",
  "com.typesafe.akka" %% "akka-stream" % "2.5.11",
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.2.1",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.0"
)