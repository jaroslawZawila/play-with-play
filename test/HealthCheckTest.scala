import org.scalatestplus.play._

import controllers.Application
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.{JsValue, JsObject}

class HealthCheckSpec extends PlaySpec {

  lazy val app: Application = new Application

  "The /healthcheck endpoint" must {
    "say everything's fine" in {
      val result = app.healthcheck.apply(FakeRequest())
      val json: JsValue = contentAsJson(result)
      json match {
        case o:JsObject => o.fields.toMap.apply("isHealthy").as[Boolean] mustBe true
        case _ => fail("not a json object")
      }
    }

  }

  "The /ping endpoint" must {
    "return pong" in {
      val result = app.ping.apply(FakeRequest())
      val bodyText = contentAsString(result)
      bodyText mustBe "pong"
    }
  }
}