name := "seblm-meals"
organization := "name.lemerdy.sebastian"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala).settings(
  routesImport += "meals.application.MealsBinders._",
  routesImport += "java.time.{LocalDateTime, Year}",
)

scalaVersion := "2.13.5"

libraryDependencies ++= Seq(evolutions, guice)
libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0"
)
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.20"

libraryDependencies += "com.h2database" % "h2" % "1.4.200" % Test
libraryDependencies += "org.mockito" %% "mockito-scala-scalatest" % "1.16.37" % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9" % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
