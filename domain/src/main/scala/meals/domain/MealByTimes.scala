package meals.domain

import java.time.LocalDateTime
import java.util.UUID

case class MealByTimes(id: UUID, description: String, times: Seq[(LocalDateTime, WeekReference)])
