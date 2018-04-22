import sbt.Keys.version


fork := true
lazy val commonSettings = Seq()

resolvers ++= Seq(
  "Twitter's Repository" at "http://maven.twttr.com/"
)

lazy val api = (project in file("."))
  .settings(
    name := "tohru",
    version := "0.1",
    scalaVersion := "2.12.4",
    libraryDependencies ++= Seq(
      "org.mongodb.scala" %% "mongo-scala-driver" % "2.2.1",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.0",
      "com.twitter" %% "twitter-server" % "18.1.0",
      "com.twitter" %% "finagle-serversets" % "18.3.0",
      "com.twitter" %% "finagle-http" % "18.3.0",
      "com.twitter" %% "bijection-core" % "0.9.6",
      "com.github.finagle" %% "finch-core" % "0.18.1",
      "com.github.finagle" %% "finch-circe" % "0.18.1",
      "io.circe" %% "circe-generic" % "0.9.3",
      "io.circe" %% "circe-core" % "0.9.3"
    )
  )

  .settings(
    mainClass in assembly := Some("io.github.ksdsouza.WebServer.RestServer")
  )

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}





