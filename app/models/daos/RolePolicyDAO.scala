package models.daos

import com.google.inject.Inject
import models.entities.{Role, RolePolicy}
import models.persistence.SlickTables.{RolePolicyTable, RoleTable}
import play.api.db.slick.DatabaseConfigProvider

/**
  * Created by hariprasadk on 08/12/16.
  */

trait RolePolicyDAO extends BaseDAOWithOptionId[RolePolicyTable, RolePolicy] {
}

class RolePolicyDAOImpl @Inject() (override protected val dbConfigProvider:DatabaseConfigProvider) extends RolePolicyDAO {
}

