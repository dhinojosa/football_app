package controllers;

import models.Stadium

import javax.inject._
import play.api._
import play.api.data.Form
import play.api.data.Forms.{mapping, number, text}
import play.api.mvc._

class StadiumController @Inject() (
    val controllerComponents: ControllerComponents
) extends BaseController {
  def list() = Action { implicit request =>
    val result = List(
      Stadium(10L, "Stamford Bridge", 1203, "A", "B"),
      Stadium(12L, "Emirates Stadium", 1001, "A", "B"),
      Stadium(13L, "Ashburton Grove", 2000, "A", "B"),
      Stadium(15L, "The Dripping Pan", 4000, "A", "B")
    )
    Ok(views.html.stadiums(result))
  }

  case class StadiumData(name:String, city:String, country:String, seats:Int)

  val stadiumForm = Form(
    mapping(
      "name" -> text,
      "city" -> text,
      "country" -> text,
      "seats" -> number
    )(StadiumData.apply)//Construction
       (StadiumData.unapply) //Destructuring
  )

//  def init() = Action { implicit request =>
//
//
//
//  }
}
