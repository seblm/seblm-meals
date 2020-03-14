package meals.application

import java.time.LocalDateTime

import javax.inject.{Inject, Singleton}
import meals.domain.MealsService
import play.api.Logging
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class MealsController @Inject() (cc: ControllerComponents, mealsService: MealsService)
    extends AbstractController(cc)
    with Logging {

  implicit val ec: ExecutionContext = cc.executionContext

  def week(): Action[AnyContent] = Action.async { implicit requestHeader: RequestHeader =>
    mealsService.currentWeekMeals().map(meals => Ok(views.html.week(meals)))
  }

  def nextWeek(): Action[AnyContent] = Action.async { implicit requestHeader: RequestHeader =>
    mealsService.nextWeekMeals().map(meals => Ok(views.html.nextweek(meals)))
  }

  case class ShuffleData(mealTime: LocalDateTime, nextWeek: Boolean)

  val shuffleForm: Form[ShuffleData] = Form(
    mapping(
      "mealTime" -> localDateTime,
      "nextWeek" -> boolean
    )(ShuffleData.apply)(ShuffleData.unapply)
  )

  def shuffle(): Action[ShuffleData] = Action.async(parse.form(shuffleForm)) { implicit request =>
    logger.debug(s"shuffle(${request.body})")
    val target = if (request.body.nextWeek) routes.MealsController.nextWeek() else routes.MealsController.week()
    mealsService.shuffle(request.body.mealTime).map(_ => Redirect(target))
  }

}
