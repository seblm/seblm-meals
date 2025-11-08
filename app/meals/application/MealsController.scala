package meals.application

import meals.application.WeekMealsWrites.given
import meals.domain.{MealSuggest, MealsService, SuggestResponse, WeekDay}
import play.api.Logging
import play.api.libs.json.*
import play.api.mvc.*

import java.time.{LocalDate, LocalDateTime, Year}
import java.util.UUID
import scala.Function.const
import scala.concurrent.ExecutionContext

class MealsController(cc: ControllerComponents, mealsService: MealsService) extends AbstractController(cc), Logging:

  given ExecutionContext = cc.executionContext

  def meal(id: UUID): Action[AnyContent] = Action.async:
    mealsService.allMeals().map(meals => meals.find(_.meal.id == id).fold(NotFound)(meal => Ok(Json.toJson(meal))))

  def meals(): Action[AnyContent] = Action.async:
    mealsService.allMeals().map(meals => Ok(Json.toJson(meals)))

  def mealsApi(year: Year, week: Int): Action[AnyContent] = Action.async:
    mealsService.meals(year, week).map(meals => Ok(Json.toJson(meals)))

  private given Reads[LinkOrInsertData] = Json.reads

  def linkOrInsertApi(): Action[LinkOrInsertData] = Action.async(parse.json[LinkOrInsertData]): request =>
    mealsService.linkOrInsert(request.body.mealTime, request.body.mealDescription).map(const(Created))

  private given Reads[UnlinkMeal] = Json.reads

  def unlinkApi(): Action[UnlinkMeal] = Action.async(parse.json[UnlinkMeal]): request =>
    mealsService.delete(request.body.mealTime).map(const(NoContent))

  private given mealSuggestWrites: Writes[MealSuggest] =
    case mealSuggest: MealSuggest =>
      Json.obj(
        "count" -> JsNumber(mealSuggest.count),
        "description" -> JsString(mealSuggest.description),
        "descriptionLabel" -> JsString(mealSuggest.descriptionLabel),
        "lastused" -> JsNumber(mealSuggest.lastused)
      )

  private given Writes[SuggestResponse] =
    case suggestResponse: SuggestResponse =>
      val fiftyTwoWeeksAgo = suggestResponse.fiftyTwoWeeksAgo
        .map(mealSuggestWrites.writes)
        .fold(JsObject.empty)(m => Json.obj("fiftyTwoWeeksAgo" -> m))
      val fourWeeksAgo = suggestResponse.fourWeeksAgo
        .map(mealSuggestWrites.writes)
        .fold(JsObject.empty)(m => Json.obj("fourWeeksAgo" -> m))
      val mostRecents = Json.obj("mostRecents" -> JsArray(suggestResponse.mostRecents.map(mealSuggestWrites.writes)))
      fiftyTwoWeeksAgo ++ fourWeeksAgo ++ mostRecents

  def suggest(reference: LocalDateTime, search: Option[String]): Action[AnyContent] = Action.async:
    mealsService.suggest(reference, search).map(meals => Ok(Json.toJson(meals)))
