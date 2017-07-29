package service

import javax.inject.Inject

import client.CustomerDynamoDbClient
import com.google.inject.Singleton
import model.Customer.CustomerSave
import model.Id

@Singleton
class CustomerService @Inject()(customerDynamoDbClient: CustomerDynamoDbClient) {

  def perssist(customer: CustomerSave): Id = customerDynamoDbClient.save(customer)
}
