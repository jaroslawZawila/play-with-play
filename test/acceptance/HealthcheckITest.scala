package acceptance

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.{ GuiceOneServerPerSuite}
import play.api.test.Helpers._
import play.api.test._

class HealthcheckITest extends PlaySpec with GuiceOneServerPerSuite {

  "Ping" must {
    "returns pong" in {

      val response = await(WsTestClient.withClient(client => {
        client.url(s"http://localhost:$port/ping").execute()
      }))

      response.body must be("pong")

    }
  }

}
