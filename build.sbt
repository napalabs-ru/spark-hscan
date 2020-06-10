
organization := "ru.napalabs.spark"
name := "spark-hscan"

version := "0.1"

scalaVersion := "2.11.12"
lazy val sparkVersion = "2.3.2"
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion % Test,
  "org.apache.spark" %% "spark-sql" % sparkVersion % Provided,
  "org.scalatest" %% "scalatest" % "3.0.8" % Test,
  "com.gliwka.hyperscan" % "hyperscan" % "1.0.0"
)

publishMavenStyle := true
parallelExecution in Test := false