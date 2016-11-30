package controllers

import com.google.inject.Inject
import models.daos.{AccountsDAO, OauthAccessTokensDAO, OauthAuthorizationCodesDAO, OauthClientsDAO}
import models.entities.{Account, OauthAccessToken}
import play.api.mvc.Controller
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future
import scalaoauth2.provider._

/**
  * Created by hariprasadk on 30/11/16.
  */

class MyDataHandler @Inject()(accountsDAO : AccountsDAO,
                             oauthAuthorizationCodesDAO : OauthAuthorizationCodesDAO,
                             oauthAccessTokensDAO : OauthAccessTokensDAO,
                             oauthClientsDAO : OauthClientsDAO) extends DataHandler[Account] with OAuth2Provider  {

  override def validateClient(request: AuthorizationRequest): Future[Boolean] =  {
    request.clientCredential.fold(Future.successful(false))(clientCredential => oauthClientsDAO.validate(clientCredential.clientId,
      clientCredential.clientSecret.getOrElse(""), request.grantType))
  }

  override def getStoredAccessToken(authInfo: AuthInfo[Account]): Future[Option[AccessToken]] = {
    oauthAccessTokensDAO.findByAuthorized(authInfo.user, authInfo.clientId.getOrElse("")).map(_.map(toAccessToken))
  }

  private val accessTokenExpireSeconds = 3600
  private def toAccessToken(accessToken: OauthAccessToken) = {
    AccessToken(
      accessToken.accessToken,
      Some(accessToken.refreshToken),
      None,
      Some(accessTokenExpireSeconds),
      accessToken.createdAt
    )
  }
  override def createAccessToken(authInfo: AuthInfo[Account]): Future[AccessToken] = {
    authInfo.clientId.fold(Future.failed[AccessToken](new InvalidRequest()))
    { clientId =>
      (for {
        clientOpt <- oauthClientsDAO.findByClientId(clientId)
        toAccessToken <-  oauthAccessTokensDAO.create(authInfo.user, clientOpt.get).map(toAccessToken) if clientOpt.isDefined
      } yield toAccessToken).recover{case _ => throw new InvalidRequest()}
    }
  }



  override def findUser(request: AuthorizationRequest): Future[Option[Account]] =
    request match {
      case request: PasswordRequest =>
        accountsDAO.authenticate(request.username, request.password)
      case request: ClientCredentialsRequest =>
        request.clientCredential.fold(Future.failed[Option[Account]](new InvalidRequest())){ clientCredential =>
          for {
            maybeAccount <- oauthClientsDAO.findClientCredentials(
              clientCredential.clientId,
              clientCredential.clientSecret.getOrElse("")
            )
          } yield maybeAccount
        }
      case _ =>
        Future.successful(None)
    }

  override def findAuthInfoByRefreshToken(refreshToken: String): Future[Option[AuthInfo[Account]]] = {
    oauthAccessTokensDAO.findByRefreshToken(refreshToken).flatMap {
      case Some(accessToken) =>
        for {
          account <- accountsDAO.findById(accessToken.accountId)
          client <-  oauthClientsDAO.findById(accessToken.oauthClientId)
        } yield {
          Some(AuthInfo(
            user = account.get,
            clientId = Some(client.get.clientId),
            scope = None,
            redirectUri = None
          ))
        }
      case None => Future.failed(new InvalidRequest())
    }
  }

  override def refreshAccessToken(authInfo: AuthInfo[Account], refreshToken: String): Future[AccessToken] = {
    authInfo.clientId.fold(Future.failed[AccessToken](new InvalidRequest()))
    { clientId => (for {
      clientOpt <- oauthClientsDAO.findByClientId(clientId)
      toAccessToken <- oauthAccessTokensDAO.refresh(authInfo.user, clientOpt.get).map(toAccessToken) if clientOpt.isDefined
    } yield toAccessToken).recover { case _ => throw new InvalidClient()}}
  }

  override def findAuthInfoByCode(code: String): Future[Option[AuthInfo[Account]]] = {
    oauthAuthorizationCodesDAO.findByCode(code).flatMap {
      case Some(code) =>
        for {
          account <- accountsDAO.findById(code.accountId)
          client <-  oauthClientsDAO.findById(code.oauthClientId)
        } yield {
          Some(AuthInfo(
            user = account.get,
            clientId = Some(client.get.clientId),
            scope = None,
            redirectUri = None
          ))
        }
      case None => Future.failed(new InvalidRequest())
    }
  }

  override def deleteAuthCode(code: String): Future[Unit] = oauthAuthorizationCodesDAO.delete(code).map( _ => {})

  override def findAccessToken(token: String): Future[Option[AccessToken]] =
    oauthAccessTokensDAO.findByAccessToken(token).map(_.map(toAccessToken))

  override def findAuthInfoByAccessToken(accessToken: AccessToken): Future[Option[AuthInfo[Account]]] = {
    oauthAccessTokensDAO.findByAccessToken(accessToken.token).flatMap {
      case Some(accessToken) =>
        for {
          account <- accountsDAO.findById(accessToken.accountId)
          client <-  oauthClientsDAO.findById(accessToken.oauthClientId)
        } yield {
          Some(AuthInfo(
            user = account.get,
            clientId = Some(client.get.clientId),
            scope = None,
            redirectUri = None
          ))
        }
      case None => Future.failed(new InvalidRequest())
    }
  }

}