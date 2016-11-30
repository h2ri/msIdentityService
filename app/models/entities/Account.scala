package models.entities

import java.sql.Timestamp
import models.entities.TimeStampFormat._
import play.api.libs.json.Json



case class Account(id: Long, email: String, password: String, createdAt: Timestamp) extends BaseEntity


object Account {

  implicit val teamReads = Json.reads[Account]
  implicit val teamWrites = Json.writes[Account]
}