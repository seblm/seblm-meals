package meals.application

import meals.domain.{MealSuggest, MealsService}
import play.api.Logging
import play.api.data.Forms._
import play.api.data._
import play.api.libs.json.{Json, Writes}
import play.api.mvc._

import java.time.LocalDateTime
import javax.inject.{Inject, Singleton}
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

  def shuffleAll(): Action[AnyContent] = Action.async { implicit request =>
    logger.debug(s"shuffleAll(${request.body})")
    mealsService.shuffleAll().map(_ => Redirect(routes.MealsController.nextWeek()))
  }

  case class LinkOrInsertData(mealDescription: String, mealTime: LocalDateTime, nextWeek: Boolean)

  val linkOrInsertForm: Form[LinkOrInsertData] = Form(
    mapping(
      "mealDescription" -> text,
      "mealTime" -> localDateTime,
      "nextWeek" -> boolean
    )(LinkOrInsertData.apply)(LinkOrInsertData.unapply)
  )

  def linkOrInsert(): Action[LinkOrInsertData] = Action.async(parse.form(linkOrInsertForm)) { implicit request =>
    logger.debug(s"link(${request.body})")
    val target = if (request.body.nextWeek) routes.MealsController.nextWeek() else routes.MealsController.week()
    mealsService.linkOrInsert(request.body.mealTime, request.body.mealDescription).map(_ => Redirect(target))
  }

  case class UnlinkData(mealTime: LocalDateTime, nextWeek: Boolean)

  val unlinkForm: Form[UnlinkData] = Form(
    mapping(
      "mealTime" -> localDateTime,
      "nextWeek" -> boolean
    )(UnlinkData.apply)(UnlinkData.unapply)
  )

  def unlink(): Action[UnlinkData] = Action.async(parse.form(unlinkForm)) { implicit request =>
    logger.debug(s"unlink(${request.body})")
    val target = if (request.body.nextWeek) routes.MealsController.nextWeek() else routes.MealsController.week()
    mealsService.delete(request.body.mealTime).map(_ => Redirect(target))
  }

  private implicit val mealSuggestWrites: Writes[MealSuggest] = Json.writes[MealSuggest]

  def suggest(): Action[AnyContent] = Action.async {
    mealsService.suggest().map(meals => Ok(Json.toJson(meals)))
  }

}
