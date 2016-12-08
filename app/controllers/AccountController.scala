package controllers

import com.google.inject.Inject
import models.daos.{AccountsDAO, OauthAccessTokensDAO, OauthAuthorizationCodesDAO, OauthClientsDAO}
import models.entities.{AccountValidator, OauthAccessToken}
import play.api.mvc.BodyParsers

import scala.concurrent.duration.Duration
import scala.concurrent.{Await}
import scalaoauth2.provider._
import controllers.utils.Response
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.mvc.{Action, Controller}
import play.api.libs.json._

import scala.concurrent.Future

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

  def create = Action.async(BodyParsers.parse.json)   {implicit request =>

    request.body.validate[AccountValidator].fold(

      errors => {
        println("In Error")
        Future(new Response(false, JsError.toJson(errors)).send)
      },
      account => {

        val result = accountsDAO.create(account)
        result.map(s => {
          Await.result(issueAccessToken(new MyDataHandler(accountsDAO, oauthAuthorizationCodesDAO, oauthAccessTokensDAO, oauthClientsDAO)), Duration.Inf)

          Await.result(oauthAccessTokensDAO.findByAccountId(s.id) map {
            case Some(token: OauthAccessToken) => Ok(Json.obj("status" -> "Ok" , "payload" -> Json.toJson(token)))
            case _ => BadRequest(Json.obj("status" -> "failed"))
          }, Duration.Inf)
        })

      }
    )
  }

  def delete(id:Long) = Action.async {
    val result = accountsDAO.deleteById(id)
    result.map(msg => Ok(Json.obj("status" -> "Ok" , "message" -> Json.toJson(msg))) )
  }

}
