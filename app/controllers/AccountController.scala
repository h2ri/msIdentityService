package controllers

import com.google.inject.Inject
import models.daos.AccountsDAO
import models.entities.Account
import play.api.mvc.BodyParsers
//import play.api.libs.json.{JsError, Json}
import controllers.utils.Response
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.mvc.{Action, Controller}
import play.api.libs.json._

import scala.concurrent.Future

/**
  * Created by hariprasadk on 30/11/16.
  */
class AccountController @Inject() (accountsDAO: AccountsDAO) extends Controller{


  def show(id:Long) = Action.async { implicit request =>
    val result = accountsDAO.findById(id)
    result.map(msg => Ok(Json.obj("status" -> "Ok" , "message" -> Json.toJson(msg))) )
  }

  def list = Action.async {
    val result = accountsDAO.list()
    result.map(msg => Ok(Json.obj("status" -> "Ok" , "message" -> Json.toJson(msg))) )
  }

  def create = Action.async(BodyParsers.parse.json)  { implicit request =>
    request.body.validate[Account].fold(
      errors => {
        println("In Error")
        Future(new Response(false, JsError.toJson(errors)).send)
      },
      account => {
        val result = accountsDAO.insert(account)
        println(result)
        result.map(msg => Ok(Json.obj("status" -> "Ok" , "message" -> Json.toJson(msg))) )
      }
    )
  }


}