import play.sbt.PlayImport.guice
import sbt.Keys._
import sbt.{Project, _}

object Dependencies {

  val projectDependencies = Seq(
    guice,
    "com.typesafe"            % "config"              % "1.3.1",
    "io.kamon"                %% "kamon-core"         % "0.6.7",
    "io.kamon"                % "kamon-play-2.6_2.11" % "0.6.8",
    "com.monsanto.arch"       %% "kamon-prometheus"   % "0.2.0",
    "io.prometheus"           % "simpleclient_common" % "0.0.20",
    "ch.qos.logback"          % "logback-classic"     % "1.1.7",
    "com.typesafe.akka"       %% "akka-slf4j"         % "2.4.10",
    "me.moocar"               % "logback-gelf"        % "0.3",
    "org.slf4j"               % "slf4j-api"           % "1.7.21"
  )

  val testDependecies = Seq(
    "org.scalatestplus.play"  %% "scalatestplus-play" % "3.0.0" % "test"

  )

  val dependecies = (projectDependencies ++ testDependecies).map(
    _.exclude("org.slf4j", "slf4j-log4j12")
      .exclude("javax.jms", "jms")
      .exclude("com.sun.jdmk", "jmxtools")
      .exclude("com.sun.jmx", "jmxri")
      .exclude("org.slf4j", "slf4j-api")
      .exclude("ch.qos.logback", "logback-classic"))



  implicit class DependenciesProject(project: Project) {

    def withDependencies: Project = project
      .settings(
        libraryDependencies ++= dependecies)
  }
}
