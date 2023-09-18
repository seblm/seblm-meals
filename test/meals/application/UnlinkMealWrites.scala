package meals.application

import play.api.libs.json.{Json, Writes}

object UnlinkMealWrites {

  implicit val unlinkMealWrites: Writes[UnlinkMeal] = Json.writes

}
