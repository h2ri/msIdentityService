package models.entities

import models.entities.TimeStampFormat._
import java.sql.Timestamp


import play.api.libs.json.Json

/**
  * Created by hariprasadk on 01/12/16.
  */
case class Test(id:Long,name:String, createdAt: Timestamp) extends BaseEntity


object Test{
  implicit val testReads = Json.reads[Test]
  implicit val testWrites = Json.writes[Test]
}
