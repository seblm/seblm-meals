package meals.domain

import play.api.libs.functional.syntax.*
import play.api.libs.json.{JsPath, Reads}

import java.util.UUID

object MealReads:

  given Reads[Meal] =
    ((JsPath \ "id").read[String] and
      (JsPath \ "description").read[String] and
      (JsPath \ "url").readNullable[String] and
      (JsPath \ "image").readNullable[String]): (id, description, url, image) =>
      Meal(UUID.fromString(id), description, url, image)
