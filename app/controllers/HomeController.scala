package controllers

import javax.inject._
import play.api._
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    //I am going to do stuff
    Ok(views.html.index())
  }

  def intro(name:String) = Action { implicit request =>
      Ok(s"Hello, you have reached our hotline, you say you want the following?"
          + name)
  }

  def findById(id:Long) = Action {implicit request =>
    val result = if (id > 10) "Stuart"
    else "Laura"
    Redirect(routes.HomeController.intro(result))
  }
}
