package meals.domain

import meals.infrastructure.MealRow
import play.api.Logging

import java.time.DayOfWeek._
import java.time._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Random, Try}

class MealsServiceImpl(clock: Clock, repository: MealRepository)(implicit ec: ExecutionContext)
    extends MealsService
    with Logging {

  override def meals(year: Year, week: Int): Future[WeekMeals] = {
    val (from, to) = DatesTransformations.range(year, week)
    repository.meals(from, to).map(mealsByWeekDay(year, week, clock))
  }

  override def linkOrInsert(mealTime: LocalDateTime, mealDescription: String): Future[Meal] =
    repository.linkOrInsert(mealTime, mealDescription)

  override def shuffle(day: LocalDateTime): Future[Option[Meal]] = {
    val until = day.toLocalDate.atStartOfDay().`with`(MONDAY).minusNanos(1)
    for {
      all <- repository.all()
      keys = all.filterNot(_._2.exists(_.isAfter(until))).keys.toSeq
      newRandom <- Future.successful(Try(keys(random.nextInt(keys.length))).toOption)
      result <- newRandom.fold(Future.successful[Option[Meal]](None))(repository.link(_, day).map(Some(_)))
    } yield result
  }

  private def moreUsedThenMoreRecent(
      first: (MealRow, Seq[LocalDateTime]),
      second: (MealRow, Seq[LocalDateTime])
  ): Boolean =
    if (first._2.length != second._2.length) first._2.length > second._2.length
    else first._2.max.isAfter(second._2.max)

  override def suggest(search: Option[String]): Future[Seq[MealSuggest]] = {
    val now = LocalDateTime.now(clock)
    repository.all().map { mealsByDate =>
      val all = mealsByDate.toSeq
      val filtered = all.filter { case (meal, _) =>
        search.fold(true)(token => meal.description.contains(token))
      }
      filtered.sortWith(moreUsedThenMoreRecent).take(10).map { case (meal, dates) =>
        MealSuggest(
          count = dates.length,
          description = meal.description,
          descriptionLabel = search.fold(meal.description) { token =>
            val highlighted = s"<strong>$token</strong>"
            meal.description.split(token).mkString(highlighted) + (if (meal.description.endsWith(token)) highlighted
                                                                   else "")
          },
          lastused = Duration.between(dates.max, now).toDays.toInt
        )
      }
    }
  }

  override def delete(mealTime: LocalDateTime): Future[Unit] = repository.unlink(mealTime)

  private def mealsByWeekDay(year: Year, week: Int, clock: Clock)(meals: Seq[Meal]): WeekMeals =
    meals.foldLeft(WeekMeals(year, week, clock))((weekMeals: WeekMeals, meal: Meal) =>
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

  private val random: Random = new Random

}
