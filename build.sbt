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
    routesImport += "java.time.{LocalDate, LocalDateTime, Year}",
    scalaVersion := "3.5.1",
    Test / javaOptions += "-Dconfig.file=test/resources/application-test.conf"
  )
  .aggregate(domain)
  .dependsOn(domain)

lazy val domain = project
  .settings(
    libraryDependencies += "org.mockito" % "mockito-core" % "5.13.0" % Test,
    libraryDependencies += "org.scalatest" %% "scalatest-flatspec" % "3.2.19" % Test,
    libraryDependencies += "org.scalatest" %% "scalatest-shouldmatchers" % "3.2.19" % Test,
    scalaVersion := "3.5.1"
  )

libraryDependencies += evolutions
libraryDependencies += "org.playframework" %% "play" % "3.0.5"
libraryDependencies += "org.playframework" %% "play-configuration" % "3.0.5"
libraryDependencies += "org.playframework" %% "play-json" % "3.0.4"
libraryDependencies += "org.playframework" %% "play-slick" % "6.1.1"
libraryDependencies += "org.playframework" %% "play-slick-evolutions" % "6.1.1"
libraryDependencies += "com.typesafe.slick" %% "slick" % "3.5.2"

libraryDependencies += "org.postgresql" % "postgresql" % "42.7.4" % Runtime

libraryDependencies += "com.h2database" % "h2" % "2.3.232" % Test
libraryDependencies += "org.scalatest" %% "scalatest-flatspec" % "3.2.19" % Test
libraryDependencies += "org.scalatest" %% "scalatest-shouldmatchers" % "3.2.19" % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test

// these modules are declared by org.playframework:sbt-plugin with wrong scope Compile instead of Runtime
// raised by unusedCompileDependenciesTest
unusedCompileDependenciesFilter -= moduleFilter("org.playframework", "play-logback")
unusedCompileDependenciesFilter -= moduleFilter("org.playframework", "play-pekko-http-server")
unusedCompileDependenciesFilter -= moduleFilter("org.playframework", "play-server")
unusedCompileDependenciesFilter -= moduleFilter("org.playframework.twirl", "twirl-api")
