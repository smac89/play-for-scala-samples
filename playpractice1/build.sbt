lazy val root = (project in file(".")).enablePlugins(PlayScala).settings(
    name := "Play Intro: Play for Scala",
    organization := "com.example",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.11.11",
    fork in run := true,

    libraryDependencies ++= Seq(
        filters,
        "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test
    )
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
