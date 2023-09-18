name := "seblm-meals"
organization := "name.lemerdy.sebastian"

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(DockerPlugin, PlayScala)
  .settings(
    dockerBaseImage := "eclipse-temurin:17",
    dockerExposedPorts := Seq(9000),
    dockerEnvVars := Map(
      "APPLICATION_SECRET" -> "",
      "POSTGRESQL_ADDON_USER" -> "",
      "POSTGRESQL_ADDON_PASSWORD" -> "",
      "POSTGRESQL_ADDON_HOST" -> "",
      "POSTGRESQL_ADDON_DB" -> ""
    ),
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
    scalaVersion := "3.3.1",
    libraryDependencies += "org.mockito" % "mockito-core" % "5.5.0" % Test,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.17" % Test
  )

scalaVersion := "2.13.12"

libraryDependencies += evolutions
libraryDependencies += "com.typesafe.play" %% "play" % "2.8.20"
libraryDependencies += "com.typesafe.play" %% "play-functional" % "2.10.1"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.10.1"
libraryDependencies += "com.typesafe.play" %% "play-slick" % "5.1.0"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "5.1.0"
libraryDependencies += "com.typesafe.slick" %% "slick" % "3.4.1"

libraryDependencies += "org.postgresql" % "postgresql" % "42.6.0" % Runtime

libraryDependencies += "com.h2database" % "h2" % "2.2.222" % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.17" % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test

// these three modules are declared by com.typesafe.play:sbt-plugin with wrong scope Compile instead of Runtime
unusedCompileDependenciesFilter -= moduleFilter("com.typesafe.play", "play-akka-http-server")
unusedCompileDependenciesFilter -= moduleFilter("com.typesafe.play", "play-logback")
unusedCompileDependenciesFilter -= moduleFilter("com.typesafe.play", "play-server")
