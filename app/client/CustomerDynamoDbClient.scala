package client

import awscala._
import com.google.inject.{Inject, Singleton}
import dynamodbv2._
import model.Customer.{CustomerSave}
import model.Id

@Singleton
class CustomerDynamoDbClient @Inject() (dynamoClient: DynamoDB) {

  private implicit val client: DynamoDB = dynamoClient

  private lazy val customers = client.table("customers").get

  def save(customer: CustomerSave): Id = {
    val id = Id.random
    println(customerToSeq(customer))
    customers.putItem(id.id, customerToSeq(customer): _*)
    id
  }

  private def customerToSeq(customer: CustomerSave): Seq[Tuple2[String, Any]] = {
    Seq(
      ("firstName" -> customer.firstname),
      ("surname" -> customer.surname)
    )
  }

  //TEST

}