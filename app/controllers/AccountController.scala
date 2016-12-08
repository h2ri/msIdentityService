package controllers

import com.google.inject.Inject
import models.daos.{AccountsDAO, OauthAccessTokensDAO, OauthAuthorizationCodesDAO, OauthClientsDAO}
import models.entities.{Account, AccountValidator, OauthAccessToken}
import play.api.mvc.BodyParsers

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}
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
//        result.map( s => {
//          issueAccessToken(new MyDataHandler(accountsDAO, oauthAuthorizationCodesDAO, oauthAccessTokensDAO, oauthClientsDAO))
//          Await.result(oauthAccessTokensDAO.findById(s.id),Duration.Inf).map(msg => {
//            Ok(Json.obj("status" -> "Ok" , "message" -> Json.toJson(msg)))
//
//          })
//        })

        result.map(s => {
          Await.result(issueAccessToken(new MyDataHandler(accountsDAO, oauthAuthorizationCodesDAO, oauthAccessTokensDAO, oauthClientsDAO)), Duration.Inf)
          Await.result(oauthAccessTokensDAO.findByAccountId(s.id).map(res => {
            println(res)
            println(s.id)
            res match {
              case Some(token: OauthAccessToken) => Ok(Json.obj("status" -> "Ok" , "payload" -> Json.toJson(token)))
              case _ => BadRequest(Json.obj("status" -> "failed"))
            }
          }), Duration.Inf)
          //Ok(Json.obj("status" -> "Ok" , "message" -> Json.toJson(1)))
        })



        //.map(msg => Ok(Json.obj("status" -> "Ok" , "message" -> Json.toJson(msg))) )


        //val resultMap = result.map(r => r.id)
        //val test = issueAccessToken(new MyDataHandler(accountsDAO, oauthAuthorizationCodesDAO, oauthAccessTokensDAO, oauthClientsDAO))
        //val resultAccessToken = oauthAccessTokensDAO.findById()
        //result.map(msg => Ok(Json.obj("status" -> "Ok" , "message" -> Json.toJson(msg))) )

        //        test.map(
        //          re =>
        //            println((re.body).dataStream.map {
        //              result =>
        //                //println(result.decodeString("US-ASCII"))
        //                val s  = result.toString()
        //                println(s)
        //            })



        //test.map(  res => println(((res.body).asJava).dataStream())
        //test.map( re => (re.body).dataStream.flatMapMerge( m => m) )
        //println(test.)

         //Result(200, Map(Pragma -> no-cache, Cache-Control -> no-store))

        //result.map(msg => Ok(Json.obj("status" -> "Ok" , "message" -> Json.toJson(msg))) )
      }
    )
  }

  //def combineJson(a:Future[Result],b:)

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
