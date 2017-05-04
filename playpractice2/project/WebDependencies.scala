import sbt._

object WebDependencies {

    lazy val WebJars = Seq (
        "org.webjars" % "bootstrap" % "3.3.7",
        "org.webjars" % "jquery" % "3.2.0",
        "com.adrianhurt" %% "play-bootstrap" % "1.1-P25-B3" exclude("org.webjars", "jquery")
    )
}
