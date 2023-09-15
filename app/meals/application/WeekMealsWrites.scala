package meals.application

import meals.domain.{Meal, Titles, WeekDay, WeekMeals, WeekReference}
import play.api.libs.json.{JsObject, JsString, JsValue, Json, Writes}
import play.api.libs.json.JsValue._

object WeekMealsWrites {

  private implicit val titlesWrites: Writes[Titles] = (titles: Titles) =>
    Json.obj("short" -> titles.short, "long" -> titles.long)

  private implicit val WeekReferenceWrites: Writes[WeekReference] = (weekReference: WeekReference) =>
    Json.obj("year" -> weekReference.year.getValue, "week" -> weekReference.week, "isActive" -> weekReference.isActive)

  private implicit val MealWrites: Writes[Meal] = (meal: Meal) =>
    Json.obj("id" -> meal.id.toString, "time" -> meal.time.toString, "meal" -> meal.meal)

  private implicit val WeekDayWrites: Writes[WeekDay] = (weekDay: WeekDay) =>
    JsObject(
      Vector(
        Some("reference" -> JsString(weekDay.reference.toString)),
        weekDay.lunch.map(meal => "lunch" -> Json.toJson(meal)),
        weekDay.dinner.map(meal => "dinner" -> Json.toJson(meal))
      ).flatten
    )

  implicit val weekMealsWrites: Writes[WeekMeals] = (weekMeals: WeekMeals) =>
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

}
