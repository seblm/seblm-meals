package meals.domain

import java.time.LocalDateTime

case class WeekMeal(time: LocalDateTime, meal: Option[String])
