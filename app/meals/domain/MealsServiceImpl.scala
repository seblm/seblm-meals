package meals.domain

import java.time.DayOfWeek.MONDAY
import java.time.{Clock, ZoneId, ZonedDateTime}

import scala.concurrent.Future

class MealsServiceImpl(clock: Clock, repository: MealRepository) extends MealsService {

  override def currentWeekMeals(): Future[Seq[Meal]] =
    meals(clock.instant().atZone(ZoneId.of("Europe/Paris")))(repository)

  override def nextWeekMeals(): Future[Seq[Meal]] =
    meals(clock.instant().atZone(ZoneId.of("Europe/Paris")).plusWeeks(1))(repository)

  private def meals(reference: ZonedDateTime)(implicit repository: MealRepository): Future[Seq[Meal]] = {
    val firstDayOfWeek = reference.toLocalDate.atStartOfDay().`with`(MONDAY)
    val lastDayOfWeek = firstDayOfWeek.plusWeeks(1).minusNanos(1)

    repository.meals(firstDayOfWeek, lastDayOfWeek)
  }

}
