package meals.domain

import java.time.LocalDateTime
import java.util.UUID

case class Meal(id: UUID, time: LocalDateTime, meal: String)
