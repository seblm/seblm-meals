name := "seblm-meals"
organization := "name.lemerdy.sebastian"

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    routesImport += "meals.application.MealsBinders._",
    routesImport += "java.time.{LocalDateTime, Year}",
    routesImport += "java.util.UUID",
    scalacOptions += "-Ytasty-reader",
    Test / javaOptions += "-Dconfig.file=test/resources/application-test.conf"
  )
  .aggregate(domain)
  .dependsOn(domain)

lazy val domain = project
  .settings(
    scalaVersion := "3.1.2",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.12" % Test
  )

scalaVersion := "2.13.8"

libraryDependencies += evolutions
libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "5.0.2",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.2"
)
libraryDependencies += "org.postgresql" % "postgresql" % "42.4.0"

libraryDependencies += "com.h2database" % "h2" % "2.1.212" % Test
libraryDependencies += "org.mockito" %% "mockito-scala-scalatest" % "1.17.5" % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.12" % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
