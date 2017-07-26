package service

import javax.inject.Inject

import com.google.inject.Singleton
import model.Customer.Customer
import model.Id.Id

@Singleton
class CustomerService @Inject()() {

  def perssist(customer: Customer): Id = Id("1")
}
