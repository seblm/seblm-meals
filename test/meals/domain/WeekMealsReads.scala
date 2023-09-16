package meals.domain

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}

import java.time.{LocalDate, LocalDateTime, Year}
import java.util.UUID

object WeekMealsReads {

  private implicit val TitlesReads: Reads[Titles] =
    ((JsPath \ "short").read[String] and
      (JsPath \ "long").read[String])((short, long) => Titles(short, long))
  private implicit val WeekReferenceReads: Reads[WeekReference] =
    ((JsPath \ "year").read[Int] and
      (JsPath \ "week").read[Int] and
      (JsPath \ "isActive").read[Boolean])((year, week, isActive) => WeekReference(Year.of(year), week, isActive))
  private implicit val MealReads: Reads[Meal] =
    ((JsPath \ "id").read[String] and
      (JsPath \ "time").read[String] and
      (JsPath \ "meal").read[String])((id, time, meal) => Meal(UUID.fromString(id), LocalDateTime.parse(time), meal))
  private implicit val WeekDayReads: Reads[WeekDay] =
    ((JsPath \ "reference").read[String] and
      (JsPath \ "lunch").readNullable[Meal] and
      (JsPath \ "dinner").readNullable[Meal])((reference, lunch, dinner) =>
      WeekDay(LocalDate.parse(reference), lunch, dinner)
    )
  implicit val WeekMealsReads: Reads[WeekMeals] =
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
      (JsPath \ "sunday").read[WeekDay])(
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
    )

}
