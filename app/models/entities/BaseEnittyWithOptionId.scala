package models.entities

/**
  * Created by hariprasadk on 05/12/16.
  */
trait BaseEntityWithOptionId {
  val id : Option[Long]
  def isValid = true
}
