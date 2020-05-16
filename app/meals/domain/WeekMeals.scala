package meals.domain

import java.time.DayOfWeek.MONDAY
import java.time.LocalDateTime

case class WeekMeals(
    monday: WeekMeal,
    tuesday: WeekMeal,
    wednesday: WeekMeal,
    thursday: WeekMeal,
    friday: WeekMeal,
    saturday: WeekMeal,
    sunday: WeekMeal
)

object WeekMeals {

  def apply(reference: LocalDateTime): WeekMeals = {
    val monday = reference.toLocalDate.atStartOfDay().`with`(MONDAY).withHour(20)
    WeekMeals(
      monday = WeekMeal(monday, None),
      tuesday = WeekMeal(monday.plusDays(1), None),
      wednesday = WeekMeal(monday.plusDays(2), None),
      thursday = WeekMeal(monday.plusDays(3), None),
      friday = WeekMeal(monday.plusDays(4), None),
      saturday = WeekMeal(monday.plusDays(5), None),
      sunday = WeekMeal(monday.plusDays(6), None)
    )
  }

  def canShuffleAll(weekMeals: WeekMeals): Boolean = allWeekMeals(weekMeals).exists(_.meal.isEmpty)

  def allWeekMeals(weekMeals: WeekMeals): Seq[WeekMeal] = Seq(
    weekMeals.monday,
    weekMeals.tuesday,
    weekMeals.wednesday,
    weekMeals.thursday,
    weekMeals.friday,
    weekMeals.saturday,
    weekMeals.sunday
  )

}
