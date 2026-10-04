package meals.domain

import meals.domain.MealReads.given
import play.api.libs.functional.syntax.*
import play.api.libs.json.{JsPath, Reads}

object SuggestResponseReads:

  private given Reads[MealSuggest] = ((JsPath \ "count").read[Int] and (JsPath \ "descriptionLabel")
    .read[String] and (JsPath \ "lastUsed").read[Int] and (JsPath \ "meal").read[Meal]):
    (count, descriptionLabel, lastUsed, meal) => MealSuggest(count, descriptionLabel, lastUsed, meal)

  given Reads[SuggestResponse] = ((JsPath \ "fiftyTwoWeeksAgo").readNullable[MealSuggest] and (JsPath \ "fourWeeksAgo")
    .readNullable[MealSuggest] and (JsPath \ "mostRecents").read[Seq[MealSuggest]]):
    (fiftyTwoWeeksAgo, fourWeeksAgo, mostRecents) => SuggestResponse(fiftyTwoWeeksAgo, fourWeeksAgo, mostRecents)
