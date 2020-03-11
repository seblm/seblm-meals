package meals.domain

import java.time.DayOfWeek._
import java.time.{Clock, LocalDateTime, ZoneId, ZonedDateTime}

import meals.infrastructure.MealRow

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Random, Try}

class MealsServiceImpl(clock: Clock, repository: MealRepository)(implicit ec: ExecutionContext) extends MealsService {

  override def currentWeekMeals(): Future[WeekMeals] = {
    val now = clock.instant().atZone(ZoneId.of("Europe/Paris"))
    meals(now)(repository).map(mealsByWeekDay(now.toLocalDateTime))
  }

  override def nextWeekMeals(): Future[WeekMeals] = {
    val now = clock.instant().atZone(ZoneId.of("Europe/Paris")).plusWeeks(1)
    meals(now)(repository).map(mealsByWeekDay(now.toLocalDateTime))
  }

  override def shuffle(day: LocalDateTime): Future[Option[Meal]] = {
    val until = day.toLocalDate.atStartOfDay().`with`(MONDAY).minusNanos(1)
    for {
      all <- repository.all()
      keys = all.filterNot(_._2.exists(_.isAfter(until))).keys.toSeq
      newRandom <- Future.successful(Try(keys(random.nextInt(keys.length))).toOption)
      result <- newRandom.fold(Future.successful[Option[Meal]](None))(repository.link(_, day).map(Some(_)))
    } yield result
  }

  private def meals(reference: ZonedDateTime)(implicit repository: MealRepository): Future[Seq[Meal]] = {
    val firstDayOfWeek = reference.toLocalDate.atStartOfDay().`with`(MONDAY)
    val lastDayOfWeek = firstDayOfWeek.plusWeeks(1).minusNanos(1)

    repository.meals(firstDayOfWeek, lastDayOfWeek)
  }

  private def mealsByWeekDay(reference: LocalDateTime)(meals: Seq[Meal]): WeekMeals =
    meals.foldLeft(WeekMeals(reference))((weekMeals: WeekMeals, meal: Meal) =>
      meal.day match {
        case MONDAY    => weekMeals.copy(monday = weekMeals.monday.copy(meal = Some(meal.meal)))
        case TUESDAY   => weekMeals.copy(tuesday = weekMeals.tuesday.copy(meal = Some(meal.meal)))
        case WEDNESDAY => weekMeals.copy(wednesday = weekMeals.wednesday.copy(meal = Some(meal.meal)))
        case THURSDAY  => weekMeals.copy(thursday = weekMeals.thursday.copy(meal = Some(meal.meal)))
        case FRIDAY    => weekMeals.copy(friday = weekMeals.friday.copy(meal = Some(meal.meal)))
        case SATURDAY  => weekMeals.copy(saturday = weekMeals.saturday.copy(meal = Some(meal.meal)))
        case SUNDAY    => weekMeals.copy(sunday = weekMeals.sunday.copy(meal = Some(meal.meal)))
        case _         => weekMeals
      }
    )

  private val random: Random = new Random

}
