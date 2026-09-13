package meals.domain

import play.api.libs.functional.syntax.*
import play.api.libs.json.{JsPath, Reads}
import meals.domain.WeekMealsReads.given
import meals.domain.WeekDay

object WeekMealsCenteredAroundADayReads:

  // add `and Reads.pure(()))` unless it doesn’t compile
  given Reads[WeekMealsCenteredAroundADay] = ((JsPath \ "days").read[Seq[WeekDay]] and Reads.pure(())): (days, _) =>
    WeekMealsCenteredAroundADay(days)
