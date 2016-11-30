package controllers.utils

import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.Result
import play.api.mvc.Results._


/**
  * Created by murtuzalokhandwala on 14/11/16.
  *
  *
  */
class Response(status: Boolean, payload: JsValue) {

  def this(status: Int, payload: JsValue) = this(status==1, payload)
  def this(status: Boolean, payload: String) = this(status, Json.toJson(payload))
  def this(status: Int, payload: String) = this(status==1, Json.toJson(payload))

  def send: Result = {
    status match {
      case true  => Ok(get)
      case false => BadRequest(get)
    }
  }

  def get: JsObject = {
    status match {
      case true  => Json.obj("status" -> "success", "payload" -> payload)
      case false => Json.obj("status" -> "failure", "payload" -> payload)
    }
  }
}
