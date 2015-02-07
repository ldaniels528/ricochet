import sbt._
import sbt.Keys._
import sbtassembly.Plugin.AssemblyKeys._
import sbtassembly.Plugin._

assemblySettings

name := "radius"

organization := "com.ldaniels528"

version := "0.1"

scalaVersion := "2.11.5"

scalacOptions ++= Seq("-deprecation", "-encoding", "UTF-8", "-feature", "-target:jvm-1.6", "-unchecked",
  "-Ywarn-adapted-args", "-Ywarn-value-discard", "-Xlint")

javacOptions ++= Seq("-Xlint:deprecation", "-Xlint:unchecked", "-source", "1.7", "-target", "1.7", "-g:vars")

mainClass in assembly := Some("com.ldaniels528.radius.RadiusMain")

test in assembly := {}

jarName in assembly := "radius.jar"

// General Dependencies
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.8",
  "org.scala-lang" % "scala-library" % "2.11.5",
  "org.slf4j" % "slf4j-api" % "1.7.10"
)

// Testing Dependencies
libraryDependencies ++= Seq(
  "junit" % "junit" % "4.12" % "test",
  "org.mockito" % "mockito-all" % "1.10.19" % "test",
  "org.scalatest" %% "scalatest" % "2.2.3" % "test"
)

// define the resolvers
resolvers ++= Seq(
  "Clojars" at "http://clojars.org/repo/",
  "Clojars Project" at "http://clojars.org/org.clojars.pepijndevos/jnativehook",
  "Clojure Releases" at "http://build.clojure.org/releases/",
  "GPhat" at "https://raw.github.com/gphat/mvn-repo/master/releases/",
  "Java Net" at "http://download.java.net/maven/2/",
  "Maven Central Server" at "http://repo1.maven.org/maven2",
  "Sonatype Repository" at "http://oss.sonatype.org/content/repositories/releases/",
  "Typesafe Releases Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Typesafe Snapshots Repository" at "http://repo.typesafe.com/typesafe/snapshots/"
)
