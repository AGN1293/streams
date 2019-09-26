name := "kafka-streams_OSUSR_DGL_DFORM_I1-J-V3-INIT"

version := "0.1"

test in assembly := {}

scalaVersion := "2.12.0"

libraryDependencies ++= {
  val circeVersion = "0.12.0-M1"
  Seq(
    "org.apache.kafka" % "kafka-streams" % "2.2.0" % "compile" withSources(),
    "com.typesafe" % "config" % "1.3.4" % "compile" withSources(),
    "io.circe" %% "circe-parser" % circeVersion % "compile" withSources(),
    "io.circe" %% "circe-core" % circeVersion % "compile" withSources(),
    "io.circe" %% "circe-generic" % circeVersion % "compile" withSources(),
    "io.circe" %% "circe-generic-extras" % circeVersion % "compile" withSources(),
    "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2" % "compile" withSources(),
    "org.typelevel" %% "cats-core" % "1.5.0" % "compile" withSources(),
    "org.typelevel" %% "cats-effect" % "0.5" % "compile" withSources(),
    "ch.qos.logback" % "logback-classic" % "1.2.3" % "compile" withSources(),
    "org.slf4j" % "log4j-over-slf4j" % "1.7.25" % "compile",

    "org.scalatest" %% "scalatest" % "3.0.5" % "test" withSources(),
    "org.apache.kafka" % "kafka-streams-test-utils" % "2.2.0" % Test

  )
}