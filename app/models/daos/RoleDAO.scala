package models.daos

import com.google.inject.Inject
import models.persistence.SlickTables.RolesTable
import models.entities.Role
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.Future

/**
  * Created by hariprasadk on 29/11/16.
  */

//trait AccountsDAO extends BaseDAO[AccountsTable,Account]{
//  def authenticate(email: String, password: String): Future[Option[Account]]
//}

trait RoleDAO extends BaseDAO[RolesTable,Role]{

}


class RoleDAOImpl @Inject() (override protected val dbConfigProvider: DatabaseConfigProvider,  roleDAO : RoleDAO) extends RoleDAO{

}
