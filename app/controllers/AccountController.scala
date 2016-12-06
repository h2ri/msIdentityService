package controllers

import com.google.inject.Inject
import models.daos.{AccountsDAO, OauthAccessTokensDAO, OauthAuthorizationCodesDAO, OauthClientsDAO}
import models.entities.{Account, AccountValidator}
import play.api.mvc.BodyParsers

import scala.concurrent.ExecutionContext
import scalaoauth2.provider._
//import play.api.libs.json.{JsError, Json}
import controllers.utils.Response
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.mvc.{Action, Controller}
import play.api.libs.json._

import scala.concurrent.Future

/**
  * Created by hariprasadk on 30/11/16.
  */
class AccountController @Inject() (accountsDAO: AccountsDAO,
                                   oauthAuthorizationCodesDAO : OauthAuthorizationCodesDAO,
                                   oauthAccessTokensDAO : OauthAccessTokensDAO,
                                   oauthClientsDAO : OauthClientsDAO) extends Controller with OAuth2Provider {


  override val tokenEndpoint = new TokenEndpoint {
    override val handlers = Map(
      OAuthGrantType.AUTHORIZATION_CODE -> new AuthorizationCode(),
      OAuthGrantType.REFRESH_TOKEN -> new RefreshToken(),
      OAuthGrantType.CLIENT_CREDENTIALS -> new ClientCredentials(),
      OAuthGrantType.PASSWORD -> new Password()
    )
  }


  def show(id:Long) = Action.async { implicit request =>
    val result = accountsDAO.findById(id)
    result.map(msg => Ok(Json.obj("status" -> "Ok" , "message" -> Json.toJson(msg))) )
  }

  def list = Action.async {
    val result = accountsDAO.list()
    result.map(msg => Ok(Json.obj("status" -> "Ok" , "message" -> Json.toJson(msg))) )
  }

//  def cleanUserForm(data: Map[String, Seq[String]]): Map[String, Object] = {
//
//    data + ("password" -> "hari")
////    data.map{ case (key, values) =>
////      if(key == "password") (key, "hari") // trim whitespace from email
////      else (key, values)
////    }
//
//  }

//  def cleanUserForm(data : JsValue) : JsValue = {
//    data.as[JsObject] + ("password" -> Json.toJson("hari"))
//  }

  def create = Action.async(BodyParsers.parse.json)   {implicit request =>

    request.body.validate[AccountValidator].fold(

      errors => {
        println("In Error")
        Future(new Response(false, JsError.toJson(errors)).send)
      },
      account => {

        //val reuquestBodyWithEncrptedPassword = request.bod
        //val requestBody = cleanUserForm(request.body)
        val result = accountsDAO.create(account)
        val test = issueAccessToken(new MyDataHandler(accountsDAO,oauthAuthorizationCodesDAO,oauthAccessTokensDAO,oauthClientsDAO))
        //println(test.)
        test
        //result.map(msg => Ok(Json.obj("status" -> "Ok" , "message" -> Json.toJson(msg))) )
      }
    )
  }

//  def modifyBody(requestBody : JsValue)



//  def update(id:Long) = Action.async(BodyParsers.parse.json)  { implicit request =>
//    request.body.validate[AccountValidator].fold(
//
//        errors => {
//          println("In Error")
//          Future(new Response(false, JsError.toJson(errors)).send)
//        },
//      account => {
//        val result = accountsDAO.update(account)
//        println(result)
//        result.map(msg => Ok(Json.obj("status" -> "Ok" , "message" -> Json.toJson(msg))) )
//      }
//    )
//  }

  def delete(id:Long) = Action.async {
    val result = accountsDAO.deleteById(id)
    result.map(msg => Ok(Json.obj("status" -> "Ok" , "message" -> Json.toJson(msg))) )
  }

}