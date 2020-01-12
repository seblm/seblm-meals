package meals

import javax.inject.{Inject, Singleton}
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class MealsController @Inject()(mealsDAO: MealsDAO, cc: ControllerComponents) extends AbstractController(cc) {

  implicit val mealFormat: Format[Meal] = Json.format[Meal]

  implicit val executor: ExecutionContext = cc.executionContext

  def all: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    mealsDAO.allMeals().map(meals => Ok(Json.toJson(meals)))
  }

}
