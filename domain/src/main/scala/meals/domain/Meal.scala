package meals.domain

import java.util.UUID

case class Meal(id: UUID, description: String, url: Option[String])
