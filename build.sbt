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
    scalaVersion := "3.2.1",
    libraryDependencies += "org.mockito" % "mockito-core" % "5.1.0" % Test,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" % Test
  )

scalaVersion := "2.13.10"

libraryDependencies += evolutions
libraryDependencies += "com.typesafe.play" %% "play" % "2.8.18"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.9.3"
libraryDependencies += "com.typesafe.play" %% "play-slick" % "5.1.0"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "5.1.0"
libraryDependencies += "com.typesafe.slick" %% "slick" % "3.4.1"

libraryDependencies += "org.postgresql" % "postgresql" % "42.5.1" % Runtime

libraryDependencies += "com.h2database" % "h2" % "2.1.214" % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test

// these three modules are declared by com.typesafe.play:sbt-plugin with wrong scope Compile instead of Runtime
unusedCompileDependenciesFilter -= moduleFilter("com.typesafe.play", "play-akka-http-server")
unusedCompileDependenciesFilter -= moduleFilter("com.typesafe.play", "play-logback")
unusedCompileDependenciesFilter -= moduleFilter("com.typesafe.play", "play-server")
