organization := "ru.napalabs.spark"
name := "spark-hscan"

pomExtra:= <description>hyperscan wrapper for spark to allow matching large numbers (up to tens of thousands) of regular expressions</description>

scmInfo := Some(ScmInfo(url("https://github.com/napalabs-ru/spark-hscan"), "scm:git:git@github.com:napalabs-ru/spark-hscan.git"))
developers ++= List(
  Developer("ShakirzyanovArsen", " Arsen Shakirzyanov", "arsen@napalabs.ru", url("https://github.com/ShakirzyanovArsen"))
)

lazy val sparkVersion = "2.3.2"

artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
  Artifact.artifactName(sv, module, artifact).replaceAll(s"-${module.revision}", s"-${sparkVersion}${module.revision}")
}

version := "0.1"

scalaVersion := "2.11.12"
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion % Test,
  "org.apache.spark" %% "spark-sql" % sparkVersion % Provided,
  "org.scalatest" %% "scalatest" % "3.0.8" % Test,
  "com.gliwka.hyperscan" % "hyperscan" % "1.0.0"
)

parallelExecution in Test := false

description := ""
licenses := List("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))
homepage := Some(url("https://github.com/napalabs-ru/spark-hscan"))

credentials += Credentials(Path.userHome / ".sbt" / "sonatype_credentials")

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

pomIncludeRepository := { _ => false }
publishMavenStyle := true