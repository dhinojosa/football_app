package controllers;

import play.api.data.Form
import play.api.data.Forms.{mapping, number, text}
import play.api.mvc._
import services.AsyncStadiumService

import javax.inject._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.hashing.MurmurHash3

case class StadiumData(name: String, city: String, country: String, seats: Int)

class StadiumController @Inject() (
    val controllerComponents: ControllerComponents,
    val stadiumService: AsyncStadiumService
) extends BaseController
    with play.api.i18n.I18nSupport {
  def list() = Action.async { implicit request =>
    stadiumService.findAll().map(xs => Ok(views.html.stadium.stadiums(xs))
    )
  }

  val stadiumForm = Form(
    mapping(
      "name" -> text,
      "city" -> text,
      "country" -> text,
      "seats" -> number
    )(StadiumData.apply) //Construction
    (StadiumData.unapply) //Destructuring
  )

  def init(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.stadium.create(stadiumForm))
  }

  def create() = Action.async { implicit request =>
    stadiumForm.bindFromRequest.fold(
      formWithErrors => {
        println("Nay!" + formWithErrors)
        Future(BadRequest(views.html.stadium.create(formWithErrors)))
      },
      stadiumData => {
        val id = MurmurHash3.stringHash(stadiumData.name)
        val newStadium = models.Stadium(
          id,
          stadiumData.name,
          stadiumData.seats,
          stadiumData.city,
          stadiumData.country
        )
        println("Yay!" + newStadium)
        stadiumService.create(newStadium).map(v => Redirect(routes.StadiumController.show(id)))
      }
    )
  }



  def show(id: Long): Action[AnyContent] = Action.async { implicit request =>
    stadiumService
      .findById(id)
      .map {
          case Some(stadium) => Ok(views.html.stadium.show(stadium))
          case None => NotFound("Stadium not found")
      }
  }
}
