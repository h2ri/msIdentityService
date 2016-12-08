package models.daos

import com.google.inject.Inject
import models.persistence.SlickTables.RoleTable
//import models.persistence.SlickTables.RolesTable
import models.entities.Role
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.Future

/**
  * Created by hariprasadk on 29/11/16.
  */

trait RoleDAO extends BaseDAOWithOptionId[RoleTable, Role] {
}

class RoleDAOImpl @Inject() (override protected val dbConfigProvider:DatabaseConfigProvider) extends RoleDAO {
}
