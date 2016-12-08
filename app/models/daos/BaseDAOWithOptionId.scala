package models.daos

/**
  * Created by hariprasadk on 05/12/16.
  */
//import models.entities.BaseEntity
import models.entities.BaseEntityWithOptionId
import models.persistence.SlickTables.{BaseTableWithOptionId => a}
import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.lifted.{CanBeQueryCondition, TableQuery}
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext


trait AbstractBaseDAOWithOptionId[T,A] {
  def list(): Future[Seq[A]]
  def insert(row : A): Future[Option[Long]]
  def insert(rows : Seq[A]): Future[Seq[Option[Long]]]
  def update(row : A): Future[Int]
  def update(rows : Seq[A]): Future[Unit]
  def findById(id : Option[Long]): Future[Option[A]]
  def findByFilter[C : CanBeQueryCondition](f: (T) => C): Future[Seq[A]]
  def deleteById(id : Long): Future[Int]
  def deleteById(ids : Seq[Long]): Future[Int]
  def deleteByFilter[C : CanBeQueryCondition](f:  (T) => C): Future[Int]
}


abstract class BaseDAOWithOptionId[T <: a[A], A <: BaseEntityWithOptionId]()(implicit val tableQ: TableQuery[T]) extends AbstractBaseDAOWithOptionId[T,A] with HasDatabaseConfigProvider[JdbcProfile] {
  import dbConfig.driver.api._

  override def list(): Future[Seq[A]] = {
    db.run(tableQ.result)
  }

  def insert(row : A): Future[Option[Long]] ={
    insert(Seq(row)).map(_.head)
  }

  def insert(rows : Seq[A]): Future[Seq[Option[Long]]] ={
    db.run(tableQ returning tableQ.map(_.id) ++= rows.filter(_.isValid))
  }

  def update(row : A): Future[Int] = {
    if (row.isValid)
      db.run(tableQ.filter(_.id === row.id).update(row))
    else
      Future{0}
  }

  def update(rows : Seq[A]): Future[Unit] = {
    db.run(DBIO.seq((rows.filter(_.isValid).map(r => tableQ.filter(_.id === r.id).update(r))): _*))
  }

  def findById(id : Option[Long]): Future[Option[A]] = {
    db.run(tableQ.filter(_.id === id).result.headOption)
  }

  def findByFilter[C : CanBeQueryCondition](f: (T) => C): Future[Seq[A]] = {
    db.run(tableQ.withFilter(f).result)
  }

  def deleteById(id : Long): Future[Int] = {
    deleteById(Seq(id))
  }

  def deleteById(ids : Seq[Long]): Future[Int] = {
    db.run(tableQ.filter(_.id.inSet(ids)).delete)
  }

  def deleteByFilter[C : CanBeQueryCondition](f:  (T) => C): Future[Int] = {
    db.run(tableQ.withFilter(f).delete)
  }

}