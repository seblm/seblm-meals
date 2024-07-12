package meals.application

import meals.domain.WeekDay

import java.time.LocalDate

case class MealsDayResponse(previous: Option[LocalDate], meals: Seq[WeekDay], next: Option[LocalDate])
