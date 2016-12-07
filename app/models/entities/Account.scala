package models.entities

import java.sql.Timestamp

import models.entities.TimeStampFormat._
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads, Writes}



case class Account(id: Long, email: String, password: String, createdAt: Timestamp) extends BaseEntity

case class AccountValidator(email: String, password: String)


object AccountValidator {

  implicit val accountReads: Reads[AccountValidator] = (
    (JsPath \ "username").read[String] and
      (JsPath \ "password").read[String]
    ) (AccountValidator.apply _)

  implicit val accountWrites: Writes[AccountValidator] = (
    //(JsPath \ "id").write[Option[Long]] and
    (JsPath \ "username").write[String] and
      (JsPath \ "password").write[String]
    ) (unlift(AccountValidator.unapply _))
}
  object Account{
    implicit val teamReads = Json.reads[Account]
    implicit val teamWrites = Json.writes[Account]
  }
