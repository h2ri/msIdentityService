package models.entities

import java.sql.Timestamp

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads, Writes}

//case class Role(id:Long, role_name:String, oauthClientId: Long, createdAt: Timestamp) extends BaseEntity

case class Role(id:Option[Long],
                name: String,
                parent_id: Option[Long]) extends BaseEntityWithOptionId

object Role {
  implicit val roleReads: Reads[Role] = (
    (JsPath \ "id").readNullable[Long] and
      (JsPath \ "name").read[String] and
      (JsPath \ "parent_id").readNullable[Long]
    ) (Role.apply _)

  implicit val roleWrites: Writes[Role] = (
    (JsPath \ "id").writeNullable[Long] and
      (JsPath \ "name").write[String] and
      (JsPath \ "parent_id").writeNullable[Long]
    ) (unlift(Role.unapply _))

}