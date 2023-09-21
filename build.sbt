import scala.sys.process.Process

name := "seblm-meals"
organization := "name.lemerdy.sebastian"

version := "1.0-SNAPSHOT"

lazy val npmBuildTask = taskKey[Unit]("Task to build the application")

lazy val root = (project in file("."))
  .enablePlugins(DockerPlugin, PlayScala)
  .settings(
    Assets / unmanagedResourceDirectories += baseDirectory.value / "frontend" / "build",
    Docker / stage := (Docker / stage dependsOn npmBuildTask).value,
    dockerBaseImage := "eclipse-temurin:17",
    dockerExposedPorts := Seq(9000),
    dockerEnvVars := Map(
      "APPLICATION_SECRET" -> "",
      "POSTGRESQL_ADDON_USER" -> "",
      "POSTGRESQL_ADDON_PASSWORD" -> "",
      "POSTGRESQL_ADDON_HOST" -> "",
      "POSTGRESQL_ADDON_DB" -> ""
    ),
    npmBuildTask := {
      val dir = baseDirectory.value / "frontend"
      if (!((baseDirectory.value / "frontend" / "node_modules").exists() || Process("npm install", dir).! == 0) ||
        Process("npm run build", dir).! != 0) {
        throw new Exception("Build failed!")
      }
    },
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
libraryDependencies += "com.typesafe.play" %% "play-functional" % "2.9.4"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.9.4"
libraryDependencies += "com.typesafe.play" %% "play-slick" % "5.1.0"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "5.1.0"
libraryDependencies += "com.typesafe.slick" %% "slick" % "3.4.1"

libraryDependencies += "org.postgresql" % "postgresql" % "42.6.0" % Runtime

libraryDependencies += "com.h2database" % "h2" % "2.2.224" % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.17" % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test

// these three modules are declared by com.typesafe.play:sbt-plugin with wrong scope Compile instead of Runtime
// raised by unusedCompileDependenciesTest
unusedCompileDependenciesFilter -= moduleFilter("com.typesafe.play", "play-akka-http-server")
unusedCompileDependenciesFilter -= moduleFilter("com.typesafe.play", "play-logback")
unusedCompileDependenciesFilter -= moduleFilter("com.typesafe.play", "play-server")
unusedCompileDependenciesFilter -= moduleFilter("com.typesafe.play", "twirl-api")
