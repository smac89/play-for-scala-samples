import WebDependencies._

lazy val commonSettings = Seq(
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.11.11"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala).settings (
    commonSettings,
    name := "Play Practice2 Web App: Play for Scala",
    organization := "com.example",
    fork in run := true,
    routesGenerator := InjectedRoutesGenerator,

    libraryDependencies ++= WebJars ++ Seq(
        filters,
        "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test,
        "net.sf.barcode4j" % "barcode4j" % "2.1"
    )
)


// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
