package models.daos

import java.security.MessageDigest
import java.sql.Timestamp

import com.google.inject.Inject
import models.entities.{Account, AccountValidator, OauthAccessToken, OauthClient}
import models.persistence.SlickTables.AccountsTable
import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait AccountsDAO extends BaseDAO[AccountsTable,Account]{
  def authenticate(email: String, password: String): Future[Option[Account]]
  def create(account: AccountValidator): Future[Account]
}

class AccountsDAOImpl @Inject()(override protected val dbConfigProvider: DatabaseConfigProvider) extends AccountsDAO  {

  import dbConfig.driver.api._

  private def digestString(s: String): String = {
    val md = MessageDigest.getInstance("SHA-1")
    md.update(s.getBytes)
    md.digest.foldLeft("") { (s, b) =>
      s + "%02x".format(if (b < 0) b + 256 else b)
    }
  }

  def authenticate(email: String, password: String): Future[Option[Account]] = {
    val hashedPassword = digestString(password)
    findByFilter( acc => acc.password === hashedPassword && acc.email === email).map(_.headOption)
  }

  override def create(account: AccountValidator): Future[Account] = {
    val createdAt = new Timestamp(new DateTime().getMillis)
    val nAccount = new Account(
      id = 0,
      email = account.email,
      password = account.password,
      createdAt = createdAt
    )
    insert(nAccount).map(id => nAccount.copy(id = id))
  }
}
