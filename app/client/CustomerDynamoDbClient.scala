package client

import awscala._
import dynamodbv2._
import model.Id.Id
import play.api.libs.json.{JsValue, Json}

object CustomerDynamoDbClient {

  implicit val dynamoDB = DynamoDB("AKIAIDGUHWBW3U3ZXOVQ","8RwXUKOpIG27DGgaxobIJk2fcKSb9JELQ3MRXK4B")(Region.EU_WEST_1)

    val customerTable: TableMeta = dynamoDB.createTable(
      name = "customers",
      hashPK = "Id" -> AttributeType.String,
      rangePK = "Id2" -> AttributeType.Number,
      otherAttributes = Seq("Id2" -> AttributeType.String),
      indexes = Seq(LocalSecondaryIndex(
        name = "id2-index",
        keySchema = Seq(KeySchema("Id", KeyType.Hash), KeySchema("Id2", KeyType.Range)),
        projection = Projection(ProjectionType.Include, Seq("Id2"))
      ))
    )

  val table: Table = dynamoDB.table("customers").get

  table.put("a", "firstName" -> "jarek", "surname" -> "zawila")
  table.put("b", "firstName" -> "jarek2", "surname" -> "zawila2")
  table.put("c", "firstName" -> "jarek3", "surname" -> "zawila3")
  table.putItem("d",  Json.toJson(Id("1")).as[Map[String, JsValue]])

  val x: Map[String, JsValue] = Json.toJson(Id("1")).as[Map[String, JsValue]]
  val y = x.seq.map { (x) => (x._1, (x._1, x._2)) }   .values
  table.rangePK
  table.put("d", y)

  table.destroy()
}
