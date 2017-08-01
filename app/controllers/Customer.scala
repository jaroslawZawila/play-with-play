package controllers

import com.google.inject.{Inject, Singleton}
import model.Customer.CustomerSave
import model.Id
import play.api.mvc.{AbstractController, BodyParser, ControllerComponents}
import service.CustomerService

@Singleton
class Customer @Inject()(cc: ControllerComponents,
                         customerService: CustomerService) extends AbstractController(cc) {

  def get = ???

  def save() = Action(parse.json) { implicit request =>
    request.body.validate[CustomerSave]
        .fold(
          error => BadRequest("Bad request"),
          customer => {
            val customerID: Id = customerService.perssist(customer)
            Ok(customerID)
          }
        )
  }

  def list = ??? // add

}
