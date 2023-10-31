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
    routesImport += "meals.application.MealsBinders._",
    routesImport += "java.time.{LocalDateTime, Year}",
    routesImport += "java.util.UUID",
    scalaVersion := "3.3.1",
    Test / javaOptions += "-Dconfig.file=test/resources/application-test.conf"
  )
  .aggregate(domain)
  .dependsOn(domain)

lazy val domain = project
  .settings(
    libraryDependencies += "org.mockito" % "mockito-core" % "5.6.0" % Test,
    libraryDependencies += "org.scalatest" %% "scalatest-flatspec" % "3.2.17" % Test,
    libraryDependencies += "org.scalatest" %% "scalatest-shouldmatchers" % "3.2.17" % Test,
    scalaVersion := "3.3.1"
  )

libraryDependencies += evolutions
libraryDependencies += "org.playframework" %% "play" % "3.0.0"
libraryDependencies += "org.playframework" %% "play-configuration" % "3.0.0"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.10.2"
libraryDependencies += "com.typesafe.play" %% "play-slick" % "5.2.0-RC1"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "5.2.0-RC1"
libraryDependencies += "com.typesafe.slick" %% "slick" % "3.5.0-M4"

libraryDependencies += "org.postgresql" % "postgresql" % "42.6.0" % Runtime

libraryDependencies += "com.h2database" % "h2" % "2.2.224" % Test
libraryDependencies += "org.scalatest" %% "scalatest-flatspec" % "3.2.17" % Test
libraryDependencies += "org.scalatest" %% "scalatest-shouldmatchers" % "3.2.17" % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test

// these three modules are declared by org.playframework:sbt-plugin with wrong scope Compile instead of Runtime
// raised by unusedCompileDependenciesTest
unusedCompileDependenciesFilter -= moduleFilter("org.playframework", "play-logback")
unusedCompileDependenciesFilter -= moduleFilter("org.playframework", "play-pekko-http-server")
unusedCompileDependenciesFilter -= moduleFilter("org.playframework", "play-server")
unusedCompileDependenciesFilter -= moduleFilter("org.playframework.twirl", "twirl-api")
