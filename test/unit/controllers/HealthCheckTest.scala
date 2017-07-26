package unit.controllers

import controllers.HealthCheck
import model.HealthCheck.HealthCheckResponse
import org.scalatestplus.play.PlaySpec
import play.api.test.FakeRequest
import play.api.test.Helpers._

class HealthCheckTest extends PlaySpec {

  "HealthCheck" must {

    "replay to ping request" in {
      val controller = new HealthCheck(stubControllerComponents())

      val result = controller.ping.apply(FakeRequest())
      contentAsString(result) must equal("pong")
    }

    "replay to healthcheck request" in {
      val controller = new HealthCheck(stubControllerComponents())
      val result = controller.healthCheck.apply(FakeRequest())
      val body = contentAsJson(result).as[HealthCheckResponse]
      body.isHealthy must be(true)
      body.name must be("play-with-paly")
    }
  }

}
