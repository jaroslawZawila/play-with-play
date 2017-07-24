import sbt.Keys._
import sbt.{Project, _}

object TestingInfo {

  private val settings = Seq(
    fork in Test := true,
    testOptions in Test := Seq(Tests.Filter(unitFilter), Tests.Argument("-oF")),
    testOptions in IT := Seq(Tests.Filter(itFilter)),
    testOptions in ITLocal := Seq(Tests.Filter(itFilter)),
    testOptions in ITDocker := Seq(Tests.Filter(itDockerFilter)),

    fork in run := true,
    javaOptions in run += s"-Dconfig.file=${Option(System.getProperty("config.file")).getOrElse("conf/application.conf")}",
    javaOptions in run += s"-Dlogback.configurationFile=${Option(System.getProperty("logback.configurationFile")).getOrElse("conf/logback.xml")}",

    javaOptions in IT += "-Dconfig.file=conf/uat/application.conf",

    javaOptions in ITLocal += "-Dconfig.file=conf/uat/application.conf",
    javaOptions in ITLocal += "-Dlogback.configurationFile=conf/logback.xml",

    javaOptions in ITDocker += "-Dconfig.file=conf/uat/application.conf"
)

  lazy val IT = config("itUat") extend(Test)
  lazy val ITLocal = config("itLocal") extend(Test)
  lazy val ITDocker = config("itDocker") extend(Test)

  def itFilter(name: String): Boolean = name endsWith "ITest"
  def itDockerFilter(name: String): Boolean = name endsWith "ITDockerTest"
  def unitFilter(name: String): Boolean = !itFilter(name) && !itDockerFilter(name)

  implicit class TestingInfoProject(project: Project) {

    def withTestingInfo: Project = project
      .configs(IT)
      .settings(inConfig(IT)(Defaults.testTasks): _*)
      .configs(ITDocker)
      .settings(inConfig(ITDocker)(Defaults.testTasks): _*)
      .configs(ITLocal)
      .settings(inConfig(ITLocal)(Defaults.testTasks): _*)
      .settings(settings: _*)
  }
}