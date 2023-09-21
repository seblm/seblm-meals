package meals.application

import meals.application.WeekMealsWrites._
import meals.domain.{MealSuggest, MealsService, SuggestResponse}
import play.api.Logging
import play.api.libs.json._
import play.api.mvc._

import java.time.{LocalDateTime, Year}
import scala.Function.const
import scala.concurrent.ExecutionContext

class MealsController(cc: ControllerComponents, mealsService: MealsService)
    extends AbstractController(cc)
    with Logging {

  implicit val ec: ExecutionContext = cc.executionContext

  def mealsApi(year: Year, week: Int): Action[AnyContent] = Action.async {
    mealsService.meals(year, week).map(meals => Ok(Json.toJson(meals)))
  }

  private implicit val linkOrInsertDataReads: Reads[LinkOrInsertData] = Json.reads

  def linkOrInsertApi(): Action[LinkOrInsertData] = Action.async(parse.json[LinkOrInsertData]) { request =>
    mealsService.linkOrInsert(request.body.mealTime, request.body.mealDescription).map(const(Created))
  }

  private implicit val unlinkMealReads: Reads[UnlinkMeal] = Json.reads

  def unlinkApi(): Action[UnlinkMeal] = Action.async(parse.json[UnlinkMeal]) { request =>
    mealsService.delete(request.body.mealTime).map(const(NoContent))
  }

  private implicit val mealSuggestWrites: Writes[MealSuggest] = { case mealSuggest: MealSuggest =>
    Json.obj(
      "count" -> JsNumber(mealSuggest.count),
      "description" -> JsString(mealSuggest.description),
      "descriptionLabel" -> JsString(mealSuggest.descriptionLabel),
      "lastused" -> JsNumber(mealSuggest.lastused)
    )
  }

  private implicit val suggestResponseWrites: Writes[SuggestResponse] = { case suggestResponse: SuggestResponse =>
    val fiftyTwoWeeksAgo = suggestResponse.fiftyTwoWeeksAgo
      .map(mealSuggestWrites.writes)
      .fold(JsObject.empty)(m => Json.obj("fiftyTwoWeeksAgo" -> m))
    val fourWeeksAgo = suggestResponse.fourWeeksAgo
      .map(mealSuggestWrites.writes)
      .fold(JsObject.empty)(m => Json.obj("fourWeeksAgo" -> m))
    val mostRecents = Json.obj("mostRecents" -> JsArray(suggestResponse.mostRecents.map(mealSuggestWrites.writes)))
    fiftyTwoWeeksAgo ++ fourWeeksAgo ++ mostRecents
  }

  def suggest(reference: LocalDateTime, search: Option[String]): Action[AnyContent] = Action.async {
    mealsService.suggest(reference, search).map(meals => Ok(Json.toJson(meals)))
  }

}
