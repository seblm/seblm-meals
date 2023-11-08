package meals.application

import play.api.libs.json.{Json, Writes}

object UnlinkMealWrites:

  given Writes[UnlinkMeal] = Json.writes
