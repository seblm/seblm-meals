addSbtPlugin("com.github.cb372" % "sbt-explicit-dependencies" % "0.2.16")
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.18")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.0")

// avoid org.scala-lang.modules:scala-xml_2.12:2.1.0 (early-semver) is selected over {1.2.0, 1.1.1}
libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
