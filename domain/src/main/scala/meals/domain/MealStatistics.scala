package meals.domain

import java.time.LocalDateTime

case class MealStatistics(count: Int, first: LocalDateTime, last: LocalDateTime, meal: Meal)
