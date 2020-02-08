package controllers

import javax.inject.{Inject, Singleton}
import meals.MealsDAO
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, Request}

import scala.concurrent.ExecutionContext

@Singleton
class AllController @Inject() (cc: ControllerComponents, mealsDAO: MealsDAO) extends AbstractController(cc) {

  implicit val ec: ExecutionContext = cc.executionContext

  def index(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    mealsDAO.allMeals().map(meals => Ok(views.html.all(meals)))
  }

}
