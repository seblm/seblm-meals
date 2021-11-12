package meals.domain

case class SuggestResponse(
    fiftyTwoWeeksAgo: Option[MealSuggest],
    fourWeeksAgo: Option[MealSuggest],
    mostRecents: Seq[MealSuggest]
)
