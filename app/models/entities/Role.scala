package models.entities

import java.sql.Timestamp

case class Role(id:Long, role_name:String, oauthClientId: Long, createdAt: Timestamp) extends BaseEntity