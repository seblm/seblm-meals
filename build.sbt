name := "seblm-meals"
organization := "name.lemerdy.sebastian"

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(DockerPlugin, PlayScala)
  .settings(
    Assets / unmanagedResourceDirectories += baseDirectory.value / "frontend" / "build",
    dockerBaseImage := "eclipse-temurin:21",
    dockerExposedPorts := Seq(9000),
    dockerEnvVars := Map(
      "APPLICATION_SECRET" -> "",
      "POSTGRESQL_ADDON_USER" -> "",
      "POSTGRESQL_ADDON_PASSWORD" -> "",
      "POSTGRESQL_ADDON_HOST" -> "",
      "POSTGRESQL_ADDON_DB" -> ""
    ),
    routesImport += "meals.application.MealsBinders.given",
    routesImport += "java.time.{LocalDateTime, Year}",
    scalaVersion := "3.3.1",
    Test / javaOptions += "-Dconfig.file=test/resources/application-test.conf"
  )
  .aggregate(domain)
  .dependsOn(domain)

lazy val domain = project
  .settings(
    libraryDependencies += "org.mockito" % "mockito-core" % "5.9.0" % Test,
    libraryDependencies += "org.scalatest" %% "scalatest-flatspec" % "3.2.17" % Test,
    libraryDependencies += "org.scalatest" %% "scalatest-shouldmatchers" % "3.2.17" % Test,
    scalaVersion := "3.3.1"
  )

lazy val catseffect = project
  .settings(
    scalaVersion := "3.3.1",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.4.14" % Runtime,
    libraryDependencies += "org.postgresql" % "r2dbc-postgresql" % "1.0.4.RELEASE",
    libraryDependencies += "org.slf4j" % "slf4j-api" % "2.0.9",
    libraryDependencies += "org.typelevel" %% "cats-effect" % "3.5.3",
    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0-M10" % Test,
    libraryDependencies += "org.typelevel" %% "munit-cats-effect" % "2.0.0-M4" % Test,
  )

libraryDependencies += evolutions
libraryDependencies += "org.playframework" %% "play" % "3.0.1"
libraryDependencies += "org.playframework" %% "play-configuration" % "3.0.1"
libraryDependencies += "org.playframework" %% "play-json" % "3.0.2"
libraryDependencies += "org.playframework" %% "play-slick" % "6.0.0-M2"
libraryDependencies += "org.playframework" %% "play-slick-evolutions" % "6.0.0-M2"
libraryDependencies += "com.typesafe.slick" %% "slick" % "3.5.0-M4"

libraryDependencies += "org.postgresql" % "postgresql" % "42.7.1" % Runtime

libraryDependencies += "com.h2database" % "h2" % "2.2.224" % Test
libraryDependencies += "org.scalatest" %% "scalatest-flatspec" % "3.2.17" % Test
libraryDependencies += "org.scalatest" %% "scalatest-shouldmatchers" % "3.2.17" % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test

// these modules are declared by org.playframework:sbt-plugin with wrong scope Compile instead of Runtime
// raised by unusedCompileDependenciesTest
unusedCompileDependenciesFilter -= moduleFilter("org.playframework", "play-logback")
unusedCompileDependenciesFilter -= moduleFilter("org.playframework", "play-pekko-http-server")
unusedCompileDependenciesFilter -= moduleFilter("org.playframework", "play-server")
unusedCompileDependenciesFilter -= moduleFilter("org.playframework.twirl", "twirl-api")
