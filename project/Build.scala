import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "itcplus"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
      "jp.t2v" %% "play20.auth" % "0.5" notTransitive(),
      "org.scalaj" %% "scalaj-time" % "0.6",
      "org.scalatest" %% "scalatest" % "1.8" % "test",
      "org.squeryl" %% "squeryl" % "0.9.5-2",
      "org.mindrot" % "jbcrypt" % "0.3m",
      "org.jsoup" % "jsoup" % "1.7.2",
      "mysql" % "mysql-connector-java" % "5.1.18"
    )
    // Only compile the bootstrap bootstrap.less file and any other *.less file in the stylesheets directory
    def customLessEntryPoints(base: File): PathFinder = (
    (base / "app" / "assets" / "stylesheets" / "bootstrap" * "bootstrap.less" ) +++
      (base / "app" / "assets" / "stylesheets" * "*.less")
    )
    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here
      testOptions in Test := Nil,
      lessEntryPoints <<= baseDirectory(customLessEntryPoints)
    )

}
