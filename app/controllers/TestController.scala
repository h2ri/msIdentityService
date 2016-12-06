package controllers

import com.google.inject.Inject
import models.daos.{TestDAO, TestDAOImpl}
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.mvc.{Action, Controller}
import com.typesafe.config.{Config,ConfigFactory}

/**
  * Created by hariprasadk on 01/12/16.
  */
class TestController @Inject() (testDAO : TestDAOImpl) extends Controller{

  def list = Action.async {
    val result = testDAO.list()
    result.map(msg => Ok(Json.obj("status" -> "Ok" , "message" -> Json.toJson(msg))) )
  }

}
