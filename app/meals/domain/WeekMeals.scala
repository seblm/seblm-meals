package meals.domain

import java.time.DayOfWeek._
import java.time.LocalDate

case class WeekMeals(
    monday: WeekDay,
    tuesday: WeekDay,
    wednesday: WeekDay,
    thursday: WeekDay,
    friday: WeekDay,
    saturday: WeekDay,
    sunday: WeekDay
)

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
