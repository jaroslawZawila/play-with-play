import DockerPackaging._
import ReleaseProcess._
import BuildInfo._
import TestingInfo._

val baseSettings = Seq(
  scalaVersion := "2.11.8",
  resolvers += "DL Bintray Monsanto Repository Manager" at "https://dl.bintray.com/monsanto/maven",
  resolvers += "Mvn repository" at "https://mvnrepository.com/artifact/",
  organization := "com.ovoenergy",
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
  watchSources ~= { _.filterNot(f => f.getName.endsWith(".swp") || f.getName.endsWith(".swo") || f.isDirectory) },
  ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) },
  shellPrompt := { state =>
    val branch = if(file(".git").exists){
      "git branch".lines_!.find{_.head == '*'}.map{_.drop(1)}.getOrElse("")
    }else ""
    Project.extract(state).currentRef.project + branch + " > "
  },
  resolvers ++= Seq(Opts.resolver.sonatypeReleases)
)

lazy val root = Project(
  "play-with-paly", file(".")
).enablePlugins(PlayScala).settings(
  baseSettings: _*
).settings(
  libraryDependencies ++= Seq(
    guice,
    "com.typesafe" % "config" % "1.3.1",
    "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % "test",
    "io.kamon" %% "kamon-core" % "0.6.7",
    "io.kamon" % "kamon-play-2.6_2.11" % "0.6.8",
    "com.monsanto.arch" %% "kamon-prometheus" % "0.2.0",
    "io.prometheus" % "simpleclient_common" % "0.0.20"
  ).map(
  _.exclude("org.slf4j", "slf4j-log4j12")
   .exclude("javax.jms", "jms")
   .exclude("com.sun.jdmk", "jmxtools")
   .exclude("com.sun.jmx", "jmxri")
   .exclude("org.slf4j", "slf4j-api")
   .exclude("ch.qos.logback", "logback-classic")),
  libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-classic" % "1.1.7",
    "com.typesafe.akka" %% "akka-slf4j" % "2.4.10",
    "me.moocar" % "logback-gelf" % "0.3",
    "org.slf4j" % "slf4j-api" % "1.7.21"
  )
).enablePlugins(GatlingPlugin)
 .withDocker
 .withReleasePipeline
 .settings(Revolver.settings)
 .settings(Lint.settings)
 .withBuildInfo
 .withTestingInfo

addCommandAlias("ci", ";clean;lint:compile;test;itLocal:test;itDocker:test")
addCommandAlias("ciWithCoverage", ";clean;coverage;lint:compile;test;itLocal:test;itDocker:test;coverageReport")


