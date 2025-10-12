package meals.domain

import play.api.libs.functional.syntax.*
import play.api.libs.json.{JsPath, Reads}

import java.time.{LocalDate, LocalDateTime, Year}
import java.util.UUID

object WeekMealsReads:

  private given Reads[Titles] =
    ((JsPath \ "short").read[String] and (JsPath \ "long").read[String]): (short, long) =>
      Titles(short, long)
  private given Reads[WeekReference] =
    ((JsPath \ "year").read[Int] and
      (JsPath \ "week").read[Int] and
      (JsPath \ "isActive").read[Boolean]): (year, week, isActive) =>
      WeekReference(Year.of(year), week, isActive)
  private given Reads[Meal] =
    ((JsPath \ "id").read[String] and
      (JsPath \ "description").read[String] and
      (JsPath \ "url").readNullable[String]): (id, description, url) =>
      Meal(UUID.fromString(id), description, url)
  private given Reads[MealEntry] =
    ((JsPath \ "meal").read[Meal] and (JsPath \ "time").read[String]): (meal, time) =>
      MealEntry(meal, LocalDateTime.parse(time))
  given Reads[MealStatistics] =
    ((JsPath \ "count").read[Int] and
      (JsPath \ "first").read[String] and
      (JsPath \ "last").read[String] and
      (JsPath \ "meal").read[Meal]): (count, first, last, meal) =>
      MealStatistics(count, LocalDateTime.parse(first), LocalDateTime.parse(last), meal)
  given Reads[WeekDay] =
    ((JsPath \ "reference").read[String] and
      (JsPath \ "lunch").readNullable[MealEntry] and
      (JsPath \ "dinner").readNullable[MealEntry]): (reference, lunch, dinner) =>
      WeekDay(LocalDate.parse(reference), lunch, dinner)
  given Reads[WeekMeals] =
    ((JsPath \ "titles").read[Titles] and
      (JsPath \ "previous").read[WeekReference] and
      (JsPath \ "now").read[WeekReference] and
      (JsPath \ "next").read[WeekReference] and
      (JsPath \ "monday").read[WeekDay] and
      (JsPath \ "tuesday").read[WeekDay] and
      (JsPath \ "wednesday").read[WeekDay] and
      (JsPath \ "thursday").read[WeekDay] and
      (JsPath \ "friday").read[WeekDay] and
      (JsPath \ "saturday").read[WeekDay] and
      (JsPath \ "sunday").read[WeekDay]):
      (
          titles: Titles,
          previous: WeekReference,
          current: WeekReference,
          next: WeekReference,
          monday: WeekDay,
          tuesday: WeekDay,
          wednesday: WeekDay,
          thursday: WeekDay,
          friday: WeekDay,
          saturday: WeekDay,
          sunday: WeekDay
      ) => WeekMeals(titles, previous, current, next, monday, tuesday, wednesday, thursday, friday, saturday, sunday)
