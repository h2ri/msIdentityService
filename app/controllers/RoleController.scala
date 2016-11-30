package controllers



import com.google.inject.Inject
import models.daos._
import play.api.mvc.{Action, Controller}

import scalaoauth2.provider.OAuth2Provider


class RoleController @Inject()(accountsDAO : AccountsDAO,
                               oauthAuthorizationCodesDAO : OauthAuthorizationCodesDAO,
                               oauthAccessTokensDAO : OauthAccessTokensDAO,
                               oauthClientsDAO : OauthClientsDAO
                           )   extends Controller with OAuth2Provider {


  def list = Action{ implicit request =>
  //  println(roleDAO.findById(request.id))
    Ok("Test Done")
  }


}