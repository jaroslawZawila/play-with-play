package controllers

import javax.inject.{Inject, Singleton}

import com.ovoenergy.build.BuildInfo
import model.HealthCheck.HealthCheckResponse
import play.api.libs.json._
import play.api.mvc._

@Singleton
class HealthCheck @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def healthCheck = Action {
    val hcr = HealthCheckResponse("play-with-paly", BuildInfo.version, true, Map())
    val json = Json.toJson(hcr)
    Ok(json)
  }

  def ping = Action {
    Ok("pong")
  }

}
