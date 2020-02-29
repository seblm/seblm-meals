package meals.application

import javax.inject.{Inject, Singleton}
import meals.domain.MealsService
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.concurrent.ExecutionContext

@Singleton
class NextWeekController @Inject() (cc: ControllerComponents, mealsService: MealsService)
    extends AbstractController(cc) {

  implicit val ec: ExecutionContext = cc.executionContext

  def index(): Action[AnyContent] = Action.async {
    mealsService.nextWeekMeals().map(meals => Ok(views.html.nextweek(meals)))
  }

}
