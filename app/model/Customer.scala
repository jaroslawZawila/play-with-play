package model

import play.api.http.{ContentTypeOf, ContentTypes, Writeable}
import play.api.libs.json.Json
import play.api.mvc.Codec


object Customer {

  trait Customer

  final case class CustomerSave(firstname: String, surname: String) extends Customer

  implicit val csWrites = Json.writes[CustomerSave]
  implicit val csReads = Json.reads[CustomerSave]
}

final case class Id(id: String)

object Id {

  def random = Id(java.util.UUID.randomUUID.toString)

  implicit val idWrites = Json.writes[Id]
  implicit val idReads = Json.reads[Id]

  implicit def writeable(implicit codec: Codec): Writeable[Id] = {
    Writeable(data => codec.encode(Json.toJson(data).toString()))
  }

  implicit def contentType(implicit codec: Codec): ContentTypeOf[Id] = {
    ContentTypeOf(Some(ContentTypes.JSON))
  }
}
