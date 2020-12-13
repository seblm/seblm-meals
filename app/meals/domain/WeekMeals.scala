package meals.domain

import java.time.DayOfWeek._
import java.time.{LocalDate, LocalDateTime}
import scala.collection.immutable.ListMap

case class WeekMeals(
    monday: WeekDay,
    tuesday: WeekDay,
    wednesday: WeekDay,
    thursday: WeekDay,
    friday: WeekDay,
    saturday: WeekDay,
    sunday: WeekDay
) {

  def allByDate: Map[LocalDateTime, Option[Meal]] = ListMap(
    monday.reference.atTime(12, 0) -> monday.lunch,
    monday.reference.atTime(20, 0) -> monday.dinner,
    tuesday.reference.atTime(12, 0) -> tuesday.lunch,
    tuesday.reference.atTime(20, 0) -> tuesday.dinner,
    wednesday.reference.atTime(12, 0) -> wednesday.lunch,
    wednesday.reference.atTime(20, 0) -> wednesday.dinner,
    thursday.reference.atTime(12, 0) -> thursday.lunch,
    thursday.reference.atTime(20, 0) -> thursday.dinner,
    friday.reference.atTime(12, 0) -> friday.lunch,
    friday.reference.atTime(20, 0) -> friday.dinner,
    saturday.reference.atTime(12, 0) -> saturday.lunch,
    saturday.reference.atTime(20, 0) -> saturday.dinner,
    sunday.reference.atTime(12, 0) -> sunday.lunch,
    sunday.reference.atTime(20, 0) -> sunday.dinner
  )

}

case class WeekDay(reference: LocalDate, lunch: Option[Meal], dinner: Option[Meal])

object WeekMeals {

  def apply(reference: LocalDate): WeekMeals =
    WeekMeals(
      monday = WeekDay(reference.`with`(MONDAY), None, None),
      tuesday = WeekDay(reference.`with`(TUESDAY), None, None),
      wednesday = WeekDay(reference.`with`(WEDNESDAY), None, None),
      thursday = WeekDay(reference.`with`(THURSDAY), None, None),
      friday = WeekDay(reference.`with`(FRIDAY), None, None),
      saturday = WeekDay(reference.`with`(SATURDAY), None, None),
      sunday = WeekDay(reference.`with`(SUNDAY), None, None)
    )

  def canShuffleAll(weekMeals: WeekMeals): Boolean = allWeekMeals(weekMeals).isEmpty

  def allWeekMeals(weekMeals: WeekMeals): Seq[Meal] =
    Seq(
      weekMeals.monday.lunch,
      weekMeals.monday.dinner,
      weekMeals.tuesday.lunch,
      weekMeals.tuesday.dinner,
      weekMeals.wednesday.lunch,
      weekMeals.wednesday.dinner,
      weekMeals.thursday.lunch,
      weekMeals.thursday.dinner,
      weekMeals.friday.lunch,
      weekMeals.friday.dinner,
      weekMeals.saturday.lunch,
      weekMeals.saturday.dinner,
      weekMeals.sunday.lunch,
      weekMeals.sunday.dinner
    ).flatten

}
