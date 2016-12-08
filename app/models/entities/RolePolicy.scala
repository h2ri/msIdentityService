package models.entities

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads, Writes}

/**
  * Created by hariprasadk on 08/12/16.
  */
case class RolePolicy (id : Option[Long], role_id: Option[Long], policy_id: Option[Long]) extends BaseEntityWithOptionId

object RolePolicy {
  implicit val rolePolicyReads: Reads[RolePolicy] = (
    (JsPath \ "id").readNullable[Long] and
      (JsPath \ "role_id").readNullable[Long] and
      (JsPath \ "policy_id").readNullable[Long]
    ) (RolePolicy.apply _)

  implicit val rolePolicyWrites: Writes[RolePolicy] = (
    (JsPath \ "id").writeNullable[Long] and
      (JsPath \ "role_id").writeNullable[Long] and
      (JsPath \ "policy_id").writeNullable[Long]
    ) (unlift(RolePolicy.unapply _))

}
