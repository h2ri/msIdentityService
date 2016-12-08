package models.daos

import com.google.inject.{ImplementedBy, Inject}
import models.entities.Policy
import models.persistence.SlickTables.PolicyTable
import play.api.db.slick.DatabaseConfigProvider


/**
  * Created by hariprasadk on 08/12/16.
  */

//@ImplementedBy(classOf[PolicyDAOImpl])
trait PolicyDAO extends BaseDAOWithOptionId[PolicyTable,Policy]{
}

class PolicyDAOImpl @Inject() (override protected val dbConfigProvider : DatabaseConfigProvider) extends PolicyDAO{

}
