import sbt._
import Keys._

object TheCafebabeExperimentBuild extends Build {

  lazy val root = Project(id = "TCE",
    base = file(".")) dependsOn (cmdTools)

  lazy val utils = Project(id = "utils", base = file("Utilities")) settings(
      libraryDependencies += "org.ow2.asm" % "asm-all" % "4.1"
    )
  lazy val cmdTools = Project(id = "cmdTools", base = file("CommandTools")) dependsOn(utils) aggregate (utils) settings(
      libraryDependencies ++= Seq(
        "org.ow2.asm" % "asm-all" % "4.1",
        "com.github.scopt" %% "scopt" % "2.1.0"
      ),
      resolvers += "sonatype-public" at "https://oss.sonatype.org/content/groups/public"
  )
}