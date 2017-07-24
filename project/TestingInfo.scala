import sbt.Keys._
import sbt.{Project, _}

object TestingInfo {
  private val worldPayServiceKeyProperty = "-Dworldpay.servicekey=T_S_502ae17b-9fe9-4e22-b493-d44fad248d61"
  private val worldPayClientKeyProperty = "-Dworldpay.clientkey=T_C_d1895cee-d07d-49a6-9311-8482e2db9592"

  private val settings = Seq(
    fork in Test := true,
    testOptions in Test := Seq(Tests.Filter(unitFilter), Tests.Argument("-oF")),
    testOptions in IT := Seq(Tests.Filter(itFilter)),
    testOptions in ITLocal := Seq(Tests.Filter(itFilter)),
    testOptions in ITDocker := Seq(Tests.Filter(itDockerFilter)),

    fork in run := true,
    javaOptions in run += s"-Dconfig.file=${Option(System.getProperty("config.file")).getOrElse("src/main/resources/application.conf")}",
    javaOptions in run += s"-Dlogback.configurationFile=${Option(System.getProperty("logback.configurationFile")).getOrElse("src/main/resources/logback.xml")}",
    javaOptions in run += "-Dmetrics.cloudWatch.enabled=false",
    javaOptions in run += "-Dmetrics.log.enabled=false",
    javaOptions in run += worldPayServiceKeyProperty,
    javaOptions in run += worldPayClientKeyProperty,

    javaOptions in IT += "-Dconfig.file=src/main/resources/env/uat/application.conf",
    javaOptions in IT += "-Dmetrics.cloudWatch.enabled=false",
    javaOptions in IT += worldPayServiceKeyProperty,
    javaOptions in IT += worldPayClientKeyProperty,

    javaOptions in ITLocal += "-Dconfig.file=src/main/resources/env/uat/application.conf",
    javaOptions in ITLocal += "-Dlogback.configurationFile=src/main/resources/logback.xml",
    javaOptions in ITLocal += "-Dservice.testport=8080",
    javaOptions in ITLocal += "-Dservice.baseurl=localhost",
    javaOptions in ITLocal += "-Dmetrics.cloudWatch.enabled=false",
    javaOptions in ITLocal += worldPayServiceKeyProperty,
    javaOptions in ITLocal += worldPayClientKeyProperty,

    javaOptions in ITDocker += "-Dconfig.file=src/main/resources/env/uat/application.conf",
    javaOptions in ITDocker += "-Dservice.testport=8080",
    javaOptions in ITDocker += "-Dkafka.bootstrapServer=localhost:9092",
    javaOptions in ITDocker += "-Dservice.baseurl=localhost",
    javaOptions in ITDocker += "-Dmetrics.cloudWatch.enabled=false",
    javaOptions in ITDocker += worldPayServiceKeyProperty,
    javaOptions in ITDocker += worldPayClientKeyProperty
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