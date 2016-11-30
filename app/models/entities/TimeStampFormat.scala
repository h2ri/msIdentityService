package models.entities

import java.sql.Timestamp

import org.joda.time.DateTime
import play.api.libs.json.{Format, JsResult, JsValue}
import play.api.libs.json.Json._

/**
  * Created by hariprasadk on 30/11/16.
  */
class TimeStampFormat{}

object TimeStampFormat {
  def timestampToDateTime(t: Timestamp): DateTime = new DateTime(t.getTime)

  def dateTimeToTimestamp(dt: DateTime): Timestamp = new Timestamp(dt.getMillis)

  implicit val timestampFormat = new Format[Timestamp] {

    def writes(t: Timestamp): JsValue = toJson(timestampToDateTime(t))

    def reads(json: JsValue): JsResult[Timestamp] = fromJson[DateTime](json).map(dateTimeToTimestamp)

  }

}
