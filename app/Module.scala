import awscala.dynamodbv2.DynamoDB
import com.google.inject.AbstractModule

class Module extends AbstractModule{

  override def configure(): Unit = {
    val db = DynamoDB()(_root_.awscala.Region.EU_WEST_1)
    bind(classOf[DynamoDB]).toInstance(db)
  }
}
