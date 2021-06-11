name := "ticker-producer-app"

version := "0.1"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.kafka" %% "kafka" % "2.4.0",
  "com.typesafe.akka" %% "akka-actor" % "2.5.25",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.3",
  "io.circe" %% "circe-core" % "0.11.2",
  "io.circe" %% "circe-generic" % "0.11.2",
  "io.circe" %% "circe-parser" % "0.11.2")
