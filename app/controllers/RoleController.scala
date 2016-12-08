package controllers



import com.google.inject.Inject
import controllers.utils.Response
import models.daos._
import models.entities.{Role => RoleValidator}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, BodyParsers, Controller}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaoauth2.provider.OAuth2Provider


class RoleController @Inject() (roleDAO: RoleDAO) extends Controller{
  def show(id: Long) = Action.async { implicit request =>
    val result = roleDAO.findById(Some(id))
    result.map(msg => Ok(Json.obj("status" -> "Ok" , "payload" -> Json.toJson(msg))) )
  }

  def list = Action.async {
    val result = roleDAO.list()
    result.map(msg => Ok(Json.obj("status" -> "Ok" , "payload" -> Json.toJson(msg))) )
  }

  def create = Action.async(BodyParsers.parse.json)   {implicit request =>

    request.body.validate[RoleValidator].fold(

      errors => {
        println("In Error")
        Future(new Response(false, JsError.toJson(errors)).send)
      },
      policy => {
        val result = roleDAO.insert(policy)
        result.map(msg => Ok(Json.obj("status" -> "Ok" , "payload" -> Json.toJson(msg))) )
      }
    )
  }

  def delete(id:Long) = Action.async {
    val result = roleDAO.deleteById(id)
    result.map(msg => Ok(Json.obj("status" -> "Ok" , "payload" -> Json.toJson(msg))) )
  }


}