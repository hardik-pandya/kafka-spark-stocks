import sbt.Keys.libraryDependencies

name := "SparkKafkStocksAveragePrice"

version := "1.0"

scalaVersion := "2.11.8"

def dockerSettings(debugPort: Option[Int] = None) = Seq(

  dockerfile in docker := {
    val artifactSource: File = assembly.value
    val artifactTargetPath = s"/project/${artifactSource.name}"
    val scriptSourceDir = baseDirectory.value / "../scripts"
    val projectDir = "/project/"

    new Dockerfile {
      from("saumitras01/java:1.8.0_111")
      add(artifactSource, artifactTargetPath)
      copy(scriptSourceDir, projectDir)
      entryPoint(s"/project/start.sh")
      cmd(projectDir, s"${name.value}", s"${version.value}")
    }
  },
  imageNames in docker := Seq(
    ImageName(s"hpandya/${name.value}:latest")
  )
)

lazy val producer = (project in file("ticker-producer-app"))
  .enablePlugins(sbtdocker.DockerPlugin)
  .settings(
    libraryDependencies ++= Seq(
        "org.apache.kafka" %% "kafka" % "2.4.0",
        "com.typesafe.akka" %% "akka-actor" % "2.5.25",
        "ch.qos.logback" % "logback-classic" % "1.2.3",
        "com.typesafe.scala-logging" %% "scala-logging" % "3.9.3",
        "io.circe" %% "circe-core" % "0.11.2",
        "io.circe" %% "circe-generic" % "0.11.2",
        "io.circe" %% "circe-parser" % "0.11.2"
    ),
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x => MergeStrategy.first
    },
    dockerSettings()
  )

lazy val consumer = (project in file("ticker-consumer-app"))
  .enablePlugins(sbtdocker.DockerPlugin)
  .settings(
        libraryDependencies ++= {
      val sparkVersion = "2.4.5"
      Seq(

      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.3",
      "org.apache.spark" %% "spark-core" % sparkVersion,
      "org.apache.spark" %% "spark-streaming" % sparkVersion,
      "org.apache.spark" %% "spark-streaming-kafka-0-10" % sparkVersion,
      "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion,
      "org.apache.spark" %% "spark-sql" % sparkVersion)
    },
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x => MergeStrategy.first
    },
    dockerSettings()
  )
