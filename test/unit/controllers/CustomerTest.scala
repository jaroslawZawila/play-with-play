package unit.controllers

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import controllers.Customer
import model.Customer.CustomerSave
import model.Id
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.mvc.Http.HeaderNames
import service.CustomerService
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._


class CustomerTest extends PlaySpec with MockitoSugar{

  implicit val system = ActorSystem()
  implicit val materilizer = ActorMaterializer()

  val mockCustomerService = mock[CustomerService]

  "Customer" must {

    "returns bad request if invalid input" in {
      val controller = new Customer(stubControllerComponents(), mockCustomerService)
      val result = controller.save().apply(FakeRequest().withHeaders(HeaderNames.CONTENT_TYPE -> JSON))

      status(result) must be(OK)
    }

    "returns id when customer saved" in {
      val controller = new Customer(stubControllerComponents(), mockCustomerService)

      when(mockCustomerService.perssist(any())).thenReturn(Id("1"))

      val customer = Json.toJson(CustomerSave("firstName", "surname"))
      val result = controller.save().apply(FakeRequest().withHeaders(HeaderNames.CONTENT_TYPE -> JSON).withBody(customer))

      status(result) must be(BAD_REQUEST)
    }

  }

}
