package meals.domain

sealed abstract class MealTime(val time: Int)

object MealTime {

  object Lunch extends MealTime(12)

  object Dinner extends MealTime(20)

}
