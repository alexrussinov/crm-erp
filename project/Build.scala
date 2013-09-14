import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "itcplus"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
      jdbc,
      "jp.t2v" %% "play2.auth" % "0.9" notTransitive(),
      "jp.t2v" %% "play2.auth.test" % "0.9" % "test" notTransitive(),
      "org.scalaj" % "scalaj-time_2.10.0-M7" % "0.6",
      "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test",
      "org.squeryl" % "squeryl_2.10" % "0.9.5-6",
      "org.mindrot" % "jbcrypt" % "0.3m",
      "org.jsoup" % "jsoup" % "1.7.2",
      "com.typesafe" %% "play-plugins-mailer" % "2.1.0",
      "pdf" % "pdf_2.10" % "0.5",

      "com.typesafe.play" %% "play-slick" % "0.3.3",
      "mysql" % "mysql-connector-java" % "5.1.18",
      "postgresql" % "postgresql" % "9.1-901-1.jdbc4"
    )
    // Only compile the bootstrap bootstrap.less file and any other *.less file in the stylesheets directory
    def customLessEntryPoints(base: File): PathFinder = (
    (base / "app" / "assets" / "stylesheets" / "bootstrap" * "bootstrap.less" ) +++
      (base / "app" / "assets" / "stylesheets" * "*.less")
    )
    val main = play.Project(appName, appVersion, appDependencies).settings(
      // Add your own project settings here
      resolvers += Resolver.url("Violas Play Modules", url("http://www.joergviola.de/releases/"))(Resolver.ivyStylePatterns),
      testOptions in Test := Nil,
      lessEntryPoints <<= baseDirectory(customLessEntryPoints)
    )

}
