package meals.domain

import meals.infrastructure.MealRow
import play.api.Logging

import java.time.DayOfWeek._
import java.time._
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Random, Try}

class MealsServiceImpl(clock: Clock, repository: MealRepository)(implicit ec: ExecutionContext)
    extends MealsService
    with Logging {

  override def meal(id: UUID): Future[Option[MealByTimes]] =
    repository
      .meals(id)
      .map(_.foldLeft[Option[MealByTimes]](None) {
        case (Some(mealByTimes), meal) =>
          Some(mealByTimes.copy(times = mealByTimes.times :+ (meal.time, WeekReference(meal.time))))
        case (None, meal) =>
          Some(MealByTimes(meal.id, meal.meal, Seq((meal.time, WeekReference(meal.time)))))
      })

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

  private def moreRecent(
      reference: LocalDateTime
  )(first: (MealRow, Seq[LocalDateTime]), second: (MealRow, Seq[LocalDateTime])): Boolean =
    sumScores(reference)(first._2) > sumScores(reference)(second._2)

  private def sumScores(reference: LocalDateTime)(dates: Seq[LocalDateTime]) = {
    val scores = dates.map(d => DatesTransformations.score(reference, d))
    if (scores.contains(0)) 0 else scores.zipWithIndex.map { case (s, index) => s / (index + 1) }.sum
  }

  override def suggest(reference: LocalDateTime, search: Option[String]): Future[Seq[MealSuggest]] = {
    repository.all().map { mealsByDate =>
      val all = mealsByDate.toSeq
      val filtered = all
        .filter { case (meal, _) =>
          search.fold(true)(token => meal.description.contains(token))
        }
        .flatMap { case (meal, dates) =>
          val mealTimeDates = dates.filter(_.getHour == reference.getHour).filter(_.isBefore(reference))
          Option.when(mealTimeDates.nonEmpty)((meal, mealTimeDates))
        }
      filtered.sortWith(moreRecent(reference)).take(10).map { case (meal, dates) =>
        MealSuggest(
          count = dates.length,
          description = meal.description,
          descriptionLabel = search.fold(meal.description) { token =>
            val highlighted = s"<strong>$token</strong>"
            meal.description.split(token).mkString(highlighted) + (if (meal.description.endsWith(token)) highlighted
                                                                   else "")
          },
          lastused = Duration.between(dates.max, reference).toDays.toInt
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
