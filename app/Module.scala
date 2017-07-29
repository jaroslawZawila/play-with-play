import awscala.dynamodbv2.DynamoDB
import com.google.inject.{AbstractModule, Inject}
import com.typesafe.config.ConfigFactory

class Module extends AbstractModule{

  override def configure(): Unit = {
    val config =ConfigFactory.load()
    val key = config.getString("dynamodb.key")
    val secret = config.getString("dynamodb.secret")

    val db = DynamoDB(key, secret)(_root_.awscala.Region.EU_WEST_1)
    bind(classOf[DynamoDB]).toInstance(db)
  }
}
