package unit.controllers

import controllers.{Healthcheck, HealthcheckResponse}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._

class HealthcheckTest extends PlaySpec {

  implicit val hcrReads = Json.reads[HealthcheckResponse]

  "Healthcheck" must {

    "replay to ping request" in {
      val controller = new Healthcheck(stubControllerComponents())

      val result = controller.ping.apply(FakeRequest())
      contentAsString(result) must equal("pong")
    }

    "replay to healthcheck request" in {
      val controller = new Healthcheck(stubControllerComponents())
      val result = controller.healthcheck.apply(FakeRequest())
      val body = contentAsJson(result).as[HealthcheckResponse]
      body.isHealthy must be(true)
      body.name must be("play-with-paly")
    }
  }

}
