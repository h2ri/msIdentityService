package models.persistence

import java.sql.Timestamp

import models.entities._
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape

/**
  * The companion object.
  */
object SlickTables extends HasDatabaseConfig[JdbcProfile] {

  protected lazy val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import dbConfig.driver.api._

  abstract class BaseTable[T](tag: Tag, name: String) extends Table[T](tag, name) {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def createdAt = column[Timestamp]("created_at")
  }

  abstract class BaseTableWithOptionId[T](tag: Tag,name : String) extends Table[T](tag,name){
    def id = column[Option[Long]]("id", O.PrimaryKey , O.AutoInc)
  }

  class AccountsTable(tag : Tag) extends BaseTable[Account](tag, "accounts") {
    def email = column[String]("email")
    def password = column[String]("password")
    def * = (id, email, password, createdAt) <> ((Account.apply _).tupled, Account.unapply _)
  }

  implicit val accountsTableQ : TableQuery[AccountsTable] = TableQuery[AccountsTable]

  class TestTable(tag:Tag) extends BaseTable[Test](tag, "test") {
    def name = column[String]("name")
    def * = (id,name,createdAt) <> ((Test.apply _).tupled, Test.unapply _)
  }

  implicit val testTableQ : TableQuery[TestTable] = TableQuery[TestTable]

//
//  class RolesTable(tag:Tag) extends BaseTable[Role](tag, "role"){
//    def role_name = column[String]("role_name")
//    def oauthClientId = column[Long]("oauth_client_id")
//
//    def * = (id,role_name,oauthClientId,createdAt) <> (Role.tupled , Role.unapply)
//
//    def oauthClient = foreignKey(
//      "role_client_fk",
//      oauthClientId,
//      OauthClientTableQ)(_.id)
//  }
//
//  implicit val roleTableQ : TableQuery[RolesTable] = TableQuery[RolesTable]

  class OauthClientTable(tag : Tag) extends BaseTable[OauthClient](tag,"oauth_clients") {
    def ownerId = column[Long]("owner_id")
    def grantType = column[String]("grant_type")
    def clientId = column[String]("client_id")
    def clientSecret = column[String]("client_secret")
    def redirectUri = column[Option[String]]("redirect_uri")
    def * = (id, ownerId, grantType, clientId, clientSecret, redirectUri, createdAt) <> (OauthClient.tupled, OauthClient.unapply)

    def owner = foreignKey(
      "oauth_client_account_fk",
      ownerId,
      accountsTableQ)(_.id)
  }

  implicit val OauthClientTableQ : TableQuery[OauthClientTable] = TableQuery[OauthClientTable]

  class OauthAuthorizationCodeTable(tag : Tag) extends BaseTable[OauthAuthorizationCode](tag,"oauth_authorization_codes") {
    def accountId = column[Long]("account_id")
    def oauthClientId = column[Long]("oauth_client_id")
    def code = column[String]("code")
    def redirectUri = column[Option[String]]("redirect_uri")
    def * = (id, accountId, oauthClientId, code, redirectUri, createdAt) <> (OauthAuthorizationCode.tupled, OauthAuthorizationCode.unapply)

    def account = foreignKey(
      "oauth_authorization_code_account_fk",
      accountId,
      accountsTableQ)(_.id)

    def oauthClient = foreignKey(
      "oauth_authorization_code_client_fk",
      oauthClientId,
      OauthClientTableQ)(_.id)
  }

  implicit val OauthAuthorizationCodeTableQ : TableQuery[OauthAuthorizationCodeTable] = TableQuery[OauthAuthorizationCodeTable]

  class OauthAccessTokenTable(tag : Tag) extends BaseTable[OauthAccessToken](tag,"oauth_access_tokens") {
    def accountId = column[Long]("account_id")
    def oauthClientId = column[Long]("oauth_client_id")
    def accessToken = column[String]("access_token")
    def refreshToken = column[String]("refresh_token")
    def * = (id, accountId, oauthClientId, accessToken, refreshToken, createdAt) <> ((OauthAccessToken.apply _).tupled, OauthAccessToken.unapply _)



    def account = foreignKey(
      "oauth_access_token_account_fk",
      accountId,
      accountsTableQ)(_.id)

    def oauthClient = foreignKey(
      "oauth_access_token_client_fk",
      oauthClientId,
      OauthAuthorizationCodeTableQ)(_.id)

  }

  implicit val OauthAccessTokenTableQ : TableQuery[OauthAccessTokenTable] = TableQuery[OauthAccessTokenTable]

  //Policy Table
  class PolicyTable(tag : Tag) extends BaseTableWithOptionId[Policy](tag, "policy") {
    def service_id = column[Long]("service_id")
    def label = column[String]("label")

    def service = foreignKey(
      "client_id_fk",
      service_id,
      OauthClientTableQ)(_.id)

    def * = (id,service_id,label) <> ((Policy.apply _).tupled, Policy.unapply _)

  }
  implicit val policyTableQ : TableQuery[PolicyTable] = TableQuery[PolicyTable]
  //End Policy


  //Role Table
  class RoleTable(tag:Tag) extends BaseTableWithOptionId[Role](tag,"role") {
    def name = column[String]("name")
    def parent_id = column[Option[Long]]("parent_id")

    def parent = foreignKey(
      "role_as_parent",
      parent_id,
      RoleTableQ)(_.id)

    def * = (id,name,parent_id) <> ((Role.apply _).tupled, Role.unapply _)
  }

  implicit val RoleTableQ : TableQuery[RoleTable] = TableQuery[RoleTable]
  //End Role

  class RolePolicyTable(tag:Tag) extends BaseTableWithOptionId[RolePolicy](tag,"role_policy") {
    def role_id = column[Option[Long]]("role_id")
    def policy_id = column[Option[Long]]("policy_id")

    def roleReference = foreignKey(
      "role_id_fk",
      role_id,
      RoleTableQ)(_.id)

    def policyReference = foreignKey(
      "policy_id_fk",
      policy_id,
      policyTableQ)(_.id)

    def * = (id,role_id,policy_id) <>  ((RolePolicy.apply _).tupled, RolePolicy.unapply _)
  }

  implicit val RolePolicyTableQ : TableQuery[RolePolicyTable] = TableQuery[RolePolicyTable]


}
