package acceptance

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.json.JsValue
import play.api.test.Helpers._
import play.api.test._

class HealthCheckITest extends PlaySpec with GuiceOneServerPerSuite {

  "Ping" must {
    "returns pong" in {
      val response = await(WsTestClient.withClient(client => {
        client.url(s"http://localhost:$port/ping").execute()
      }))

      response.status must be(OK)
      response.body must be("pong")
    }
  }

  "HealthCheck" must {
    "be healthy" in {
      val response = await(WsTestClient.withClient(client => {
        client.url(s"http://localhost:$port/healthcheck").execute()
      }))

      response.status must be(OK)
      val json = response.body[JsValue]
      (json \ "name").get.toString() must be("\"play-with-paly\"")
      (json \ "version").get.toString() must be("\"0.1-SNAPSHOT\"")
      (json \ "isHealthy").get.validate[Boolean].get must be(true)
    }
  }

}
