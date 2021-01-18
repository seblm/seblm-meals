package meals.application

import meals.domain.DatesTransformations.yearWeek
import meals.domain.{MealSuggest, MealsService}
import play.api.Logging
import play.api.data.Forms._
import play.api.data._
import play.api.libs.json.{Json, Writes}
import play.api.mvc._

import java.time.{Clock, LocalDateTime, Year}
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class MealsController @Inject() (cc: ControllerComponents, mealsService: MealsService)
    extends AbstractController(cc)
    with Logging {

  implicit val ec: ExecutionContext = cc.executionContext

  private val clock = Clock.systemDefaultZone()

  def week(): Action[AnyContent] = Action {
    Redirect((routes.MealsController.meals _).tupled(yearWeek(clock)));
  }

  def meals(year: Year, week: Int): Action[AnyContent] = Action.async { implicit requestHeader: RequestHeader =>
    mealsService.meals(year, week).map(meals => Ok(views.html.week(meals)))
  }

  case class ShuffleData(mealTime: LocalDateTime)

  val shuffleForm: Form[ShuffleData] = Form(
    mapping(
      "mealTime" -> localDateTime
    )(ShuffleData.apply)(ShuffleData.unapply)
  )

  def shuffle(): Action[ShuffleData] = Action.async(parse.form(shuffleForm)) { implicit request =>
    logger.debug(s"shuffle(${request.body})")
    mealsService.shuffle(request.body.mealTime).map(_ => Redirect(routes.MealsController.week()))
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
    mealsService
      .linkOrInsert(request.body.mealTime, request.body.mealDescription)
      .map(_ => Redirect(routes.MealsController.week()))
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
    mealsService.delete(request.body.mealTime).map(_ => Redirect(routes.MealsController.week()))
  }

  private implicit val mealSuggestWrites: Writes[MealSuggest] = Json.writes[MealSuggest]

  def suggest(search: Option[String]): Action[AnyContent] = Action.async {
    mealsService.suggest(search).map(meals => Ok(Json.toJson(meals)))
  }

}
