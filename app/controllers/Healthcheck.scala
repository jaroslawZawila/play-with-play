package controllers

import javax.inject.{Inject, Singleton}

import com.ovoenergy.build.BuildInfo
import play.api.libs.json._
import play.api.mvc._

final case class HealthcheckResponse(name: String, version: String, isHealthy: Boolean, dependencies: Map[String, Boolean])

@Singleton
class Healthcheck @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def healthcheck = Action {
    val hcr = HealthcheckResponse("play-with-paly", BuildInfo.version, true, Map())
    implicit val hcrWrites = Json.writes[HealthcheckResponse]
    val json = Json.toJson(hcr)
    Ok(json)
  }

  def ping = Action {
    Ok("pong")
  }

}
