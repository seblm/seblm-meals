package meals.application

import play.api.libs.json.{Json, Writes}

object LinkOrInsertDataWrites {

  implicit val LinkOrInsertDataWrites: Writes[LinkOrInsertData] = Json.writes

}
