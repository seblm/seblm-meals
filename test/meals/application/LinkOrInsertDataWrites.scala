package meals.application

import play.api.libs.json.{Json, Writes}

object LinkOrInsertDataWrites:

  given Writes[LinkOrInsertData] = Json.writes
