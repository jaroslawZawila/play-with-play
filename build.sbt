import DockerPackaging._
import ReleaseProcess._
import BuildInfo._
import TestingInfo._
import Dependencies._

val baseSettings = Seq(
  scalaVersion := "2.11.8",
  resolvers += "DL Bintray Monsanto Repository Manager" at "https://dl.bintray.com/monsanto/maven",
  resolvers += "Mvn repository" at "https://mvnrepository.com/artifact/",
  resolvers ++= Seq(Opts.resolver.sonatypeReleases),

  organization := "com.test",

  parallelExecution in Test := false,
  fork in Test := true,
  fork in run := false,

  scalacOptions ++= (
    "-deprecation" ::
    "-unchecked" ::
    "-Xlint" ::
    "-language:existentials" ::
    "-language:higherKinds" ::
    "-language:implicitConversions" ::
    Nil
  ),
  scalacOptions in Test ++= Seq(
    "-Ywarn-unused-import",
    "-Xfatal-warnings"
  ),
  shellPrompt := { state =>
    val branch = if(file(".git").exists){
      "git branch".lines_!.find{_.head == '*'}.map{_.drop(1)}.getOrElse("")
    }else ""
    Project.extract(state).currentRef.project + branch + " > "
  }
)

lazy val root = Project(
  "play-with-paly", file(".")
).enablePlugins(PlayScala).settings(
  baseSettings: _*
).withDependencies
 .enablePlugins(GatlingPlugin)
 .withDocker
 .withReleasePipeline
 .settings(Revolver.settings)
 .settings(Lint.settings)
 .withBuildInfo
 .withTestingInfo


addCommandAlias("ci", ";clean;lint:compile;test;itLocal:test;itDocker:test")
addCommandAlias("ciWithCoverage", ";clean;coverage;lint:compile;test;itLocal:test;itDocker:test;coverageReport")


