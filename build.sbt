ThisBuild / version := "0.1"

ThisBuild / scalaVersion := "3.3.3"

lazy val root = (project in file("."))
  .settings(
    name := "scala-akka-playground"
  )

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % "2.9.0-M1",
  "org.slf4j" % "slf4j-api" % "2.0.13",
  "org.slf4j" % "slf4j-simple" % "2.0.13",
  "joda-time" % "joda-time" % "2.12.5",
  "org.scalatest" %% "scalatest" % "3.2.19" % Test,
  "org.scalatest" %% "scalatest-funspec" % "3.2.19" % Test

)


