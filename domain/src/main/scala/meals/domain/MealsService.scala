package meals.domain

import java.time.*
import java.time.DayOfWeek.*
import java.util.regex.Pattern
import java.util.regex.Pattern.CASE_INSENSITIVE
import scala.concurrent.{ExecutionContext, Future}
import scala.util.matching.RegexCreator

class MealsService(clock: Clock, repository: MealRepository)(using ExecutionContext):

  def mealsAround(date: LocalDate, limit: Int): Future[Seq[MealEntry]] =
    val from = date.minusDays(limit).atStartOfDay()
    val to = date.plusDays(limit + 1).atStartOfDay().minusNanos(1)
    repository.meals(from, to)

  def meals(year: Year, week: Int): Future[WeekMeals] =
    val (from, to) = DatesTransformations.range(year, week)
    repository.meals(from, to).map(mealsByWeekDay(year, week, clock))

  def linkOrInsert(mealTime: LocalDateTime, mealDescription: String): Future[MealEntry] =
    repository.linkOrInsert(mealTime, mealDescription)

  private def moreRecent(
      reference: LocalDateTime
  )(first: (Meal, Seq[LocalDateTime]), second: (Meal, Seq[LocalDateTime])): Boolean =
    sumScores(reference)(first._2) > sumScores(reference)(second._2)

  private def sumScores(reference: LocalDateTime)(dates: Seq[LocalDateTime]) =
    val scores = dates.map(d => DatesTransformations.score(reference, d))
    if (scores.contains(0)) 0 else scores.zipWithIndex.map { case (s, index) => s / (index + 1) }.sum

  def suggest(reference: LocalDateTime, search: Option[String]): Future[SuggestResponse] =
    repository.all().map { mealsByDate =>
      val all = mealsByDate.toSeq
      val filtered = all
        .filter { case (meal, _) =>
          search.fold(true)(token => meal.description.toLowerCase.contains(token.toLowerCase))
        }
        .flatMap { case (meal, dates) =>
          val mealTimeDates = dates.filter(_.getHour == reference.getHour).filter(_.isBefore(reference))
          Option.when(mealTimeDates.nonEmpty)((meal, mealTimeDates))
        }
      val fourWeeksAgo = search
        .fold(filtered)(_ => Seq.empty)
        .find { case (_, dates) => dates.contains(reference.minusWeeks(4)) }
      val fiftyTwoWeeksAgo = search
        .fold(filtered)(_ => Seq.empty)
        .find { case (_, dates) => dates.contains(reference.minusWeeks(52)) }
      SuggestResponse(
        fiftyTwoWeeksAgo = fiftyTwoWeeksAgo.map(createMealSuggest(reference, search)),
        fourWeeksAgo = fourWeeksAgo.map(createMealSuggest(reference, search)),
        mostRecents = filtered
          .filterNot { case (_, dates) =>
            dates.contains(reference.minusWeeks(4)) || dates.contains(reference.minusWeeks(52))
          }
          .sortWith(moreRecent(reference))
          .take(15)
          .map(createMealSuggest(reference, search))
      )
    }

  private def createMealSuggest(reference: LocalDateTime, search: Option[String])(
      meal: Meal,
      dates: Seq[LocalDateTime]
  ): MealSuggest = MealSuggest(
    count = dates.length,
    description = meal.description,
    descriptionLabel = search.fold(meal.description): token =>
      val caseInsensitiveToken = RegexCreator.create(Pattern.compile(s"(.*)($token)(.*)", CASE_INSENSITIVE))
      meal.description match
        case caseInsensitiveToken(prefix, matchedToken, suffix) => s"$prefix<strong>$matchedToken</strong>$suffix"
        case _                                                  => meal.description
    ,
    lastused = Duration.between(dates.max, reference).toDays.toInt
  )

  def delete(mealTime: LocalDateTime): Future[Unit] = repository.unlink(mealTime)

  private def mealsByWeekDay(year: Year, week: Int, clock: Clock)(meals: Seq[MealEntry]): WeekMeals =
    meals.foldLeft(WeekMeals(year, week, clock))((weekMeals: WeekMeals, meal: MealEntry) =>
      (meal.time.getDayOfWeek, meal.time.getHour) match {
        case (MONDAY, 12)    => weekMeals.copy(monday = weekMeals.monday.copy(lunch = Some(meal)))
        case (MONDAY, 20)    => weekMeals.copy(monday = weekMeals.monday.copy(dinner = Some(meal)))
        case (TUESDAY, 12)   => weekMeals.copy(tuesday = weekMeals.tuesday.copy(lunch = Some(meal)))
        case (TUESDAY, 20)   => weekMeals.copy(tuesday = weekMeals.tuesday.copy(dinner = Some(meal)))
        case (WEDNESDAY, 12) => weekMeals.copy(wednesday = weekMeals.wednesday.copy(lunch = Some(meal)))
        case (WEDNESDAY, 20) => weekMeals.copy(wednesday = weekMeals.wednesday.copy(dinner = Some(meal)))
        case (THURSDAY, 12)  => weekMeals.copy(thursday = weekMeals.thursday.copy(lunch = Some(meal)))
        case (THURSDAY, 20)  => weekMeals.copy(thursday = weekMeals.thursday.copy(dinner = Some(meal)))
        case (FRIDAY, 12)    => weekMeals.copy(friday = weekMeals.friday.copy(lunch = Some(meal)))
        case (FRIDAY, 20)    => weekMeals.copy(friday = weekMeals.friday.copy(dinner = Some(meal)))
        case (SATURDAY, 12)  => weekMeals.copy(saturday = weekMeals.saturday.copy(lunch = Some(meal)))
        case (SATURDAY, 20)  => weekMeals.copy(saturday = weekMeals.saturday.copy(dinner = Some(meal)))
        case (SUNDAY, 12)    => weekMeals.copy(sunday = weekMeals.sunday.copy(lunch = Some(meal)))
        case (SUNDAY, 20)    => weekMeals.copy(sunday = weekMeals.sunday.copy(dinner = Some(meal)))
        case _               => weekMeals
      }
    )
