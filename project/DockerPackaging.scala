import com.typesafe.sbt.packager.Keys._
import com.typesafe.sbt.packager.archetypes.JavaServerAppPackaging
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.Docker
import com.typesafe.sbt.packager.docker._
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport._
import sbt.Keys._
import sbt._

object DockerPackaging {

  val serviceName = "play-with-paly"

  private lazy val setupAlpine = Seq(
    Cmd("RUN", "apk --update add openjdk8-jre"),
    Cmd("RUN", "apk --update add bash"),
    Cmd("RUN", "apk --update add curl"),
    Cmd("RUN", "apk --update add jq")
  )

  private val settings = Seq(
    packageName in Docker := serviceName,
    dockerRepository := Some("293486771097.dkr.ecr.eu-west-1.amazonaws.com"),
    dockerUpdateLatest := true,
    dockerExposedPorts := Seq(8080),
    dockerBaseImage := "roylines/credstash",
    dockerCommands := dockerCommands.value.head +: setupAlpine ++: dockerCommands.value.tail,
    bashScriptExtraDefines += """addJava "-Dconfig.file=${app_home}/../conf/${environment:-local}/application.conf"""",
    bashScriptExtraDefines += """addJava "-Dlogback.configurationFile=${app_home}/../conf/${environment}/logback.xml"""",
    bashScriptExtraDefines += """addJava "-Xms256M"""",
    bashScriptExtraDefines += """addJava "-Xmx1536M"""",
    bashScriptExtraDefines += s"export SERVICE_NAME=$serviceName",
    bashScriptExtraDefines += """if [ -n "$SECRETS" ] && [ -n "$environment" ]; then
                                |  baseEnv="$SERVICE_NAME"."$environment".
                                |  items=$(credstash -r eu-west-1 get $baseEnv'*' | jq 'to_entries | .[]' -r)
                                |  keys=$(echo $items | jq .key -r)
                                |  for tkey in $keys
                                |  do
                                |  	key=$(echo ${tkey//$baseEnv/} | tr . _)
                                |    export $key=$(echo $items | jq 'select(.key=="'$tkey'") | .value' -r)
                                |  done
                                |fi""".stripMargin

  )

  implicit class DockerProject(project: Project) {

    def withDocker: Project = project
      .settings(settings: _*)
      .enablePlugins(JavaServerAppPackaging, DockerPlugin)
  }
}
