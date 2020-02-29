package meals.domain

case class WeekMeals(
    monday: Option[String] = None,
    tuesday: Option[String] = None,
    wednesday: Option[String] = None,
    thursday: Option[String] = None,
    friday: Option[String] = None,
    saturday: Option[String] = None,
    sunday: Option[String] = None
)
