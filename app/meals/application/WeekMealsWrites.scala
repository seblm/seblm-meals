package meals.application

import meals.domain.{Meal, Titles, WeekDay, WeekMeals, WeekReference}
import play.api.libs.json.{JsObject, JsString, JsValue, Json, Writes}

object WeekMealsWrites:

  given Writes[Titles] = (titles: Titles) => Json.obj("short" -> titles.short, "long" -> titles.long)

  given Writes[WeekReference] = (weekReference: WeekReference) =>
    Json.obj("year" -> weekReference.year.getValue, "week" -> weekReference.week, "isActive" -> weekReference.isActive)

  given Writes[Meal] = Json.writes

  given Writes[WeekDay] = (weekDay: WeekDay) =>
    JsObject(
      Vector(
        Some("reference" -> JsString(weekDay.reference.toString)),
        weekDay.lunch.map(meal => "lunch" -> Json.toJson(meal)),
        weekDay.dinner.map(meal => "dinner" -> Json.toJson(meal))
      ).flatten
    )

  given Writes[WeekMeals] = (weekMeals: WeekMeals) =>
    Json.obj(
      "titles" -> Json.toJson(weekMeals.titles),
      "previous" -> Json.toJson(weekMeals.previous),
      "now" -> Json.toJson(weekMeals.now),
      "next" -> Json.toJson(weekMeals.next),
      "monday" -> Json.toJson(weekMeals.monday),
      "tuesday" -> Json.toJson(weekMeals.tuesday),
      "wednesday" -> Json.toJson(weekMeals.wednesday),
      "thursday" -> Json.toJson(weekMeals.thursday),
      "friday" -> Json.toJson(weekMeals.friday),
      "saturday" -> Json.toJson(weekMeals.saturday),
      "sunday" -> Json.toJson(weekMeals.sunday)
    )
