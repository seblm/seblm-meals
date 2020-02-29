package meals.application

import javax.inject.{Inject, Singleton}
import meals.domain.MealsService
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class WeekController @Inject() (cc: ControllerComponents, mealsService: MealsService) extends AbstractController(cc) {

  implicit val ec: ExecutionContext = cc.executionContext

  def index(): Action[AnyContent] = Action.async {
    mealsService.currentWeekMeals().map(meals => Ok(views.html.week(meals)))
  }

}
