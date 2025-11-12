name := "seblm-meals"
organization := "name.lemerdy.sebastian"

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(DockerPlugin, PlayScala)
  .settings(
    Assets / unmanagedResourceDirectories += baseDirectory.value / "frontend" / "build",
    dockerBaseImage := "eclipse-temurin:25",
    dockerExposedPorts := Seq(9000),
    routesImport += "meals.application.MealsBinders.given",
    routesImport += "java.time.{LocalDate, LocalDateTime, Year}",
    routesImport += "java.util.UUID",
    scalaVersion := "3.7.4",
    Test / javaOptions += "-Dconfig.file=test/resources/application-test.conf"
  )
  .aggregate(domain)
  .dependsOn(domain)

val mockitoVersion = "5.20.0"
lazy val domain = project
  .settings(
    libraryDependencies += "org.mockito" % "mockito-core" % mockitoVersion % Test,
    libraryDependencies += "org.scalatest" %% "scalatest-flatspec" % "3.2.19" % Test,
    libraryDependencies += "org.scalatest" %% "scalatest-shouldmatchers" % "3.2.19" % Test,
    scalaVersion := "3.7.4",
    Test / fork := true,
    Test / javaOptions += s"-javaagent:${csrCacheDirectory.value.getAbsolutePath}/https/repo1.maven.org/maven2/org/mockito/mockito-core/$mockitoVersion/mockito-core-$mockitoVersion.jar"
  )

libraryDependencies += evolutions
libraryDependencies += "org.playframework" %% "play" % "3.0.9"
libraryDependencies += "org.playframework" %% "play-configuration" % "3.0.9"
libraryDependencies += "org.playframework" %% "play-json" % "3.0.6"
libraryDependencies += "org.playframework" %% "play-slick" % "6.2.0"
libraryDependencies += "org.playframework" %% "play-slick-evolutions" % "6.2.0"
libraryDependencies += "com.typesafe.slick" %% "slick" % "3.6.1"

libraryDependencies += "org.postgresql" % "postgresql" % "42.7.8" % Runtime

libraryDependencies += "com.h2database" % "h2" % "2.4.240" % Test
libraryDependencies += "org.scalatest" %% "scalatest-flatspec" % "3.2.19" % Test
libraryDependencies += "org.scalatest" %% "scalatest-shouldmatchers" % "3.2.19" % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.2" % Test

// these modules are declared by org.playframework:sbt-plugin with wrong scope Compile instead of Runtime
// raised by unusedCompileDependenciesTest
unusedCompileDependenciesFilter -= moduleFilter("org.playframework", "play-logback")
unusedCompileDependenciesFilter -= moduleFilter("org.playframework", "play-pekko-http-server")
unusedCompileDependenciesFilter -= moduleFilter("org.playframework", "play-server")
unusedCompileDependenciesFilter -= moduleFilter("org.playframework.twirl", "twirl-api")
