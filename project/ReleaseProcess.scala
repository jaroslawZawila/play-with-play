import sbt._
import Keys._
import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleaseStateTransformations._
import com.typesafe.sbt.SbtNativePackager.autoImport._
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport._

object ReleaseProcess {

  lazy val createVersionMemo = taskKey[Unit]("Create a file containing the current version string. Useful for build pipelines.")
  lazy val createAWSApplication = taskKey[Unit]("Create application definition for AWS deployment.")

  private def awsVersionDescriptor(dockerImage: String, authS3: (String, String)) =
    s"""
       |{
       |  "AWSEBDockerrunVersion": "1",
       |  "Image": {
       |    "Name": "$dockerImage",
       |    "Update": "false"
       |  },
       |  "Ports": [
       |    {
       |      "ContainerPort": "8080"
       |    }
       |  ]
       |}
    """.stripMargin

  private def pipeline(project: Project) = Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    releaseStepTask(publish in Docker in project),
    releaseStepTask(createVersionMemo),
    releaseStepTask(createAWSApplication),
    setNextVersion,
    commitNextVersion,
    pushChanges
  )

  implicit class ReleaseProject(project: Project) {

    def withReleasePipeline: Project = project
      .settings(createAWSApplication := {
        val dockerRepo = (dockerRepository in project).value
        val dockerPackage = (packageName in Docker in project).value
        val dockerImage = List(dockerRepo, Some(dockerPackage)).flatten.mkString("/")
        val dockerTag = version.value
        val image = s"$dockerImage:$dockerTag"
        val dockerrunAwsContent = awsVersionDescriptor(image, ("ovo-docker-apps", "play-with-paly/dockercfg.json"))

        val targetFile: File = target.value / "aws" / "Dockerrun.aws.json"
        val targetZip: File = target.value / "aws" / s"$dockerPackage-$dockerTag.zip"
        IO.write(targetFile, dockerrunAwsContent)
        IO.zip (List((targetFile, targetFile.name)), targetZip)
      })
      .settings(createVersionMemo := {
        val file: File = target.value / "versionmemo"
        IO.write(file, version.value)
      })
      .settings(releaseProcess := pipeline(project))
  }
}