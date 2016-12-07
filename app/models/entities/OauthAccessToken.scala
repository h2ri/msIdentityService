package models.entities

import java.sql.Timestamp


import models.entities.TimeStampFormat._

import play.api.libs.json.Json

case class OauthAccessToken(
                             id: Long,
                             accountId: Long,
                             oauthClientId: Long,
                             accessToken: String,
                             refreshToken: String,
                             createdAt: Timestamp
                           ) extends BaseEntity


object OauthAccessToken{
  implicit val teamReads = Json.reads[OauthAccessToken]
  implicit val teamWrites = Json.writes[OauthAccessToken]
}