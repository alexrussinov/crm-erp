import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "itcplus"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
      "org.squeryl" %% "squeryl" % "0.9.5-2",
      "mysql" % "mysql-connector-java" % "5.1.18"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here      
    )

}
