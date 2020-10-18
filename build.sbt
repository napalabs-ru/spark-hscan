ThisBuild / organization := "ru.napalabs.spark"
ThisBuild / name := "spark-hscan"

ThisBuild / version := "0.1"

ThisBuild / scalaVersion := "2.11.12"
lazy val sparkVersion = "2.3.2"
ThisBuild / libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion % Test,
  "org.apache.spark" %% "spark-sql" % sparkVersion % Provided,
  "org.scalatest" %% "scalatest" % "3.0.8" % Test,
  "com.gliwka.hyperscan" % "hyperscan" % "1.0.0"
)

ThisBuild / parallelExecution in Test := false

ThisBuild / description := ""
ThisBuild / licenses := List("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/napalabs-ru/spark-hscan"))

ThisBuild / publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishMavenStyle := true