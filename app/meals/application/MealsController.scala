package meals.application

import meals.domain.DatesTransformations.yearWeek
import meals.domain.{DatesTransformations, MealSuggest, MealsService}
import play.api.Logging
import play.api.data.Forms._
import play.api.data._
import play.api.libs.json.{Json, Writes}
import play.api.mvc._

import java.time.{Clock, LocalDateTime, Year}
import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class MealsController @Inject() (cc: ControllerComponents, mealsService: MealsService)
    extends AbstractController(cc)
    with Logging {

  implicit val ec: ExecutionContext = cc.executionContext

  private val clock = Clock.systemDefaultZone()

  def week(): Action[AnyContent] = Action {
    Redirect((routes.MealsController.meals _).tupled(yearWeek(LocalDateTime.now(clock))))
  }

  def meal(id: UUID): Action[AnyContent] = Action.async { implicit requestHeader: RequestHeader =>
    mealsService.meal(id).map {
      case Some(mealByTimes) => Ok(views.html.meal(mealByTimes))
      case None              => NotFound
    }
  }

  def meals(year: Year, week: Int): Action[AnyContent] = Action.async { implicit requestHeader: RequestHeader =>
    mealsService.meals(year, week).map(meals => Ok(views.html.week(meals)))
  }

  val mealTimeForm: Form[LocalDateTime] = Form(single("mealTime" -> localDateTime))

  def shuffle(): Action[LocalDateTime] = Action.async(parse.form(mealTimeForm)) { implicit request =>
    logger.debug(s"shuffle(${request.body})")
    val (year, week) = DatesTransformations.yearWeek(request.body)
    mealsService.shuffle(request.body).map(_ => Redirect(routes.MealsController.meals(year, week)))
  }

  case class LinkOrInsertData(mealDescription: String, mealTime: LocalDateTime)

  val linkOrInsertForm: Form[LinkOrInsertData] = Form(
    mapping(
      "mealDescription" -> text,
      "mealTime" -> localDateTime
    )(LinkOrInsertData.apply)(LinkOrInsertData.unapply)
  )

  def linkOrInsert(): Action[LinkOrInsertData] = Action.async(parse.form(linkOrInsertForm)) { implicit request =>
    logger.debug(s"link(${request.body})")
    val (year, week) = DatesTransformations.yearWeek(request.body.mealTime)
    mealsService
      .linkOrInsert(request.body.mealTime, request.body.mealDescription)
      .map(_ => Redirect(routes.MealsController.meals(year, week)))
  }

  def unlink(): Action[LocalDateTime] = Action.async(parse.form(mealTimeForm)) { implicit request =>
    logger.debug(s"unlink(${request.body})")
    val (year, week) = DatesTransformations.yearWeek(request.body)
    mealsService.delete(request.body).map(_ => Redirect(routes.MealsController.meals(year, week)))
  }

  private implicit val mealSuggestWrites: Writes[MealSuggest] = Json.writes[MealSuggest]

  def suggest(reference: LocalDateTime, search: Option[String]): Action[AnyContent] = Action.async {
    mealsService.suggest(reference, search).map(meals => Ok(Json.toJson(meals)))
  }

}
