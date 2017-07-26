package model

import play.api.libs.json.Json

object HealthCheck {

  final case class HealthCheckResponse(name: String, version: String, isHealthy: Boolean, dependencies: Map[String, Boolean])

  implicit val hcrWrites = Json.writes[HealthCheckResponse]
  implicit val hcrReads = Json.reads[HealthCheckResponse]
}
