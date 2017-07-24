import sbt.Keys._
import sbt.Project
import sbtbuildinfo.BuildInfoPlugin
import sbtbuildinfo.BuildInfoPlugin.autoImport._

object BuildInfo {
  private val settings = Seq(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "com.ovoenergy.build")

  implicit class BuildInfoProject(project: Project) {

    def withBuildInfo: Project = project
      .settings(settings: _*)
      .enablePlugins(BuildInfoPlugin)
  }
}