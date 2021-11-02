name := "seblm-meals"
organization := "name.lemerdy.sebastian"

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    routesImport += "meals.application.MealsBinders._",
    routesImport += "java.time.{LocalDateTime, Year}",
    routesImport += "java.util.UUID",
    scalacOptions += "-Ytasty-reader"
  )
  .aggregate(domain)
  .dependsOn(domain)

lazy val domain = project
  .settings(
    scalaVersion := "3.1.0",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % Test
  )

scalaVersion := "2.13.7"

libraryDependencies ++= Seq(evolutions, guice)
libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0"
)
libraryDependencies += "org.postgresql" % "postgresql" % "42.3.1"

libraryDependencies += "com.h2database" % "h2" % "1.4.200" % Test
libraryDependencies += "org.mockito" %% "mockito-scala-scalatest" % "1.16.46" % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
