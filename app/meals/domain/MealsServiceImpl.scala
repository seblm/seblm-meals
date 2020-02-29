package meals.domain

import java.time.DayOfWeek._
import java.time.{Clock, ZoneId, ZonedDateTime}

import scala.concurrent.{ExecutionContext, Future}

class MealsServiceImpl(clock: Clock, repository: MealRepository)(implicit ec: ExecutionContext) extends MealsService {

  override def currentWeekMeals(): Future[WeekMeals] =
    meals(clock.instant().atZone(ZoneId.of("Europe/Paris")))(repository).map(mealsByWeekDay)

  override def nextWeekMeals(): Future[WeekMeals] =
    meals(clock.instant().atZone(ZoneId.of("Europe/Paris")).plusWeeks(1))(repository).map(mealsByWeekDay)

  private def meals(reference: ZonedDateTime)(implicit repository: MealRepository): Future[Seq[Meal]] = {
    val firstDayOfWeek = reference.toLocalDate.atStartOfDay().`with`(MONDAY)
    val lastDayOfWeek = firstDayOfWeek.plusWeeks(1).minusNanos(1)

    repository.meals(firstDayOfWeek, lastDayOfWeek)
  }

  private def mealsByWeekDay(meals: Seq[Meal]): WeekMeals =
    meals.foldLeft(WeekMeals())((weekMeals: WeekMeals, meal: Meal) =>
      meal.day match {
        case MONDAY    => weekMeals.copy(monday = Some(meal.meal))
        case TUESDAY   => weekMeals.copy(tuesday = Some(meal.meal))
        case WEDNESDAY => weekMeals.copy(wednesday = Some(meal.meal))
        case THURSDAY  => weekMeals.copy(thursday = Some(meal.meal))
        case FRIDAY    => weekMeals.copy(friday = Some(meal.meal))
        case SATURDAY  => weekMeals.copy(saturday = Some(meal.meal))
        case SUNDAY    => weekMeals.copy(sunday = Some(meal.meal))
        case _         => weekMeals
      }
    )

}
