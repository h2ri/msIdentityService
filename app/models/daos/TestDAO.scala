package models.daos


import com.google.inject.Inject
import models.entities.Test
import models.persistence.SlickTables.TestTable
import play.api.db.slick.DatabaseConfigProvider

/**
  * Created by hariprasadk on 01/12/16.
  */
trait TestDAO extends BaseDAO[TestTable,Test]{
}

class TestDAOImpl @Inject() (override protected val dbConfigProvider: DatabaseConfigProvider) extends TestDAO {

  import dbConfig.driver.api._

}
