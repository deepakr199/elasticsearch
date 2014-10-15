import com.github.play2war.plugin._

name := """elasticsearch"""

version := "1.0-SNAPSHOT"

Play2WarPlugin.play2WarSettings

Play2WarKeys.servletVersion := "3.0"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  cache,
  javaWs,
  "org.json" % "json" % "20090211",
  "commons-lang" % "commons-lang" % "2.3",
  "org.apache.httpcomponents" % "httpclient" % "4.3.1",
  "commons-httpclient" % "commons-httpclient" % "3.1"
)



