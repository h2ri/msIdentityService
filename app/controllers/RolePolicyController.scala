package controllers

import com.google.inject.Inject
import controllers.utils.Response
import models.daos.RolePolicyDAO
import models.entities.{Role, RolePolicy}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, BodyParsers, Controller}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

/**
  * Created by hariprasadk on 08/12/16.
  */
class RolePolicyController @Inject() (rolePolicyDAO: RolePolicyDAO) extends Controller{
  def show(id: Long) = Action.async { implicit request =>
    val result = rolePolicyDAO.findById(Some(id))
    result.map(msg => Ok(Json.obj("status" -> "Ok" , "payload" -> Json.toJson(msg))) )
  }

  def list = Action.async {
    val result = rolePolicyDAO.list()
    result.map(msg => Ok(Json.obj("status" -> "Ok" , "payload" -> Json.toJson(msg))) )
  }

  def create = Action.async(BodyParsers.parse.json)   {implicit request =>

    request.body.validate[RolePolicy].fold(

      errors => {
        println("In Error")
        Future(new Response(false, JsError.toJson(errors)).send)
      },
      policy => {
        val result = rolePolicyDAO.insert(policy)
        result.map(msg => Ok(Json.obj("status" -> "Ok" , "payload" -> Json.toJson(msg))) )
      }
    )
  }

  def delete(id:Long) = Action.async {
    val result = rolePolicyDAO.deleteById(id)
    result.map(msg => Ok(Json.obj("status" -> "Ok" , "payload" -> Json.toJson(msg))) )
  }


}
