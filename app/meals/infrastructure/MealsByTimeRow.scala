package meals.infrastructure

import java.time.LocalDateTime
import java.util.UUID

case class MealsByTimeRow(time: LocalDateTime, mealId: UUID)
