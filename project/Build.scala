import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "itcplus"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
      jdbc, javaCore, javaJdbc,
      "jp.t2v" %% "play21.auth" % "0.7",
      "com.codahale" % "jerkson_2.9.1" % "0.5.0",
      "org.scalaj" % "scalaj-time_2.9.1" % "0.6",
      "org.scalatest" %% "scalatest" % "1.9.1" % "test",
      "org.squeryl" %% "squeryl" % "0.9.5-6",
      "org.mindrot" % "jbcrypt" % "0.3m",
      "mysql" % "mysql-connector-java" % "5.1.18"
    )
    // Only compile the bootstrap bootstrap.less file and any other *.less file in the stylesheets directory
    def customLessEntryPoints(base: File): PathFinder = (
    (base / "app" / "assets" / "stylesheets" / "bootstrap" * "bootstrap.less" ) +++
      (base / "app" / "assets" / "stylesheets" * "*.less")
    )
    val main = play.Project(appName, appVersion, appDependencies).settings(
      // Add your own project settings here

      testOptions in Test := Nil,
      lessEntryPoints <<= baseDirectory(customLessEntryPoints),
      resolvers += "repo.codahale.com" at "http://repo.codahale.com"
    )

}
