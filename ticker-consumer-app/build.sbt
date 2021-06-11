name := "ticker-consumer-app"

version := "0.1"

scalaVersion := "2.11.8"


libraryDependencies ++= {
  val sparkVersion = "2.4.5"
  Seq(

  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.3",
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % sparkVersion,
  "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion)
}
