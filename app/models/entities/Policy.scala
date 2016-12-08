package models.entities

import java.sql.Timestamp

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads, Writes}

/**
  * Created by hariprasadk on 08/12/16.
  */

case class Policy(
                   id:Option[Long],
                   service_id : Long,
                   label:String
                 ) extends BaseEntityWithOptionId


object Policy {
    implicit val policyReads: Reads[Policy] = (
      (JsPath \ "id").readNullable[Long] and
      (JsPath \ "service_id").read[Long] and
        (JsPath \ "label").read[String]
      ) (Policy.apply _)

    implicit val accountWrites: Writes[Policy] = (
      (JsPath \ "id").writeNullable[Long] and
      (JsPath \ "service_id").write[Long] and
        (JsPath \ "label").write[String]
      ) (unlift(Policy.unapply _))
}
