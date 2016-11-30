package controllers



import com.google.inject.Inject
import models.daos._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.mvc.{Action, Controller}

import scalaoauth2.provider.OAuth2Provider


class RoleController @Inject()(accountsDAO : AccountsDAO,
                               oauthAuthorizationCodesDAO : OauthAuthorizationCodesDAO,
                               oauthAccessTokensDAO : OauthAccessTokensDAO,
                               oauthClientsDAO : OauthClientsDAO)   extends Controller with OAuth2Provider {

  def list = Action.async { implicit request =>
    issueAccessToken(new MyDataHandler(accountsDAO,oauthAuthorizationCodesDAO,oauthAccessTokensDAO,oauthClientsDAO))
  }

  def list1 = Action{ implicit request =>
//    println(roleDAO.findById(request.id))
    Ok("Test Done")
  }


}