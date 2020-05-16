package meals.domain

import java.time.DayOfWeek._
import java.time.{Clock, LocalDateTime, ZoneId, ZonedDateTime}

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

  override def shuffleAll(reference: ZonedDateTime): Future[WeekMeals] = {
    val (firstDayOfWeek, lastDayOfWeek) = firstAndLastDayOfWeek(reference)
    val until = firstDayOfWeek.minusWeeks(1).minusNanos(1)

    for {
      all <- repository.all()
    } yield {
      val value = all.filter(_._2.exists(date => date.isAfter(firstDayOfWeek) && date.isBefore(lastDayOfWeek)))
      val currentMeals = value.keys.toSeq
      val previousMeals = Random.shuffle(all.filterNot(_._2.exists(_.isAfter(until))).keys.filterNot(currentMeals.contains).toSeq.take(7 - currentMeals.length))
      var index = 0
      WeekMeals(
        monday = {
          val monday = firstDayOfWeek.plusHours(20)
          WeekMeal(monday, Some(value.find(_._2.exists(_ == monday)).map(_._1.description).getOrElse {
            index += 1
            previousMeals(index - 1).description
          }))
        },
        tuesday = {
          val tuesday = firstDayOfWeek.plusHours(20).plusDays(1)
          WeekMeal(tuesday, Some(value.find(_._2.exists(_ == tuesday)).map(_._1.description).getOrElse {
            index += 1
            previousMeals(index - 1).description
          }))
        },
        wednesday = {
          val wednesday = firstDayOfWeek.plusHours(20).plusDays(2)
          WeekMeal(wednesday, Some(value.find(_._2.exists(_ == wednesday)).map(_._1.description).getOrElse {
            index += 1
            previousMeals(index - 1).description
          }))
        },
        thursday = {
          val thursday = firstDayOfWeek.plusHours(20).plusDays(3)
          WeekMeal(thursday, Some(value.find(_._2.exists(_ == thursday)).map(_._1.description).getOrElse {
            index += 1
            previousMeals(index - 1).description
          }))
        },
        friday = {
          val friday = firstDayOfWeek.plusHours(20).plusDays(4)
          WeekMeal(friday, Some(value.find(_._2.exists(_ == friday)).map(_._1.description).getOrElse {
            index += 1
            previousMeals(index - 1).description
          }))
        },
        saturday = {
          val saturday = firstDayOfWeek.plusHours(20).plusDays(5)
          WeekMeal(saturday, Some(value.find(_._2.exists(_ == saturday)).map(_._1.description).getOrElse {
            index += 1
            previousMeals(index - 1).description
          }))
        },
        sunday = {
          val sunday = firstDayOfWeek.plusHours(20).plusDays(6)
          WeekMeal(sunday, Some(value.find(_._2.exists(_ == sunday)).map(_._1.description).getOrElse {
            index += 1
            previousMeals(index - 1).description
          }))
        }
      )
    }
  }

  private def meals(reference: ZonedDateTime)(implicit repository: MealRepository): Future[Seq[Meal]] = {
    val (firstDayOfWeek, lastDayOfWeek) = firstAndLastDayOfWeek(reference)

    repository.meals(firstDayOfWeek, lastDayOfWeek)
  }

  private def firstAndLastDayOfWeek(reference: ZonedDateTime): (LocalDateTime, LocalDateTime) = {
    val firstDayOfWeek = reference.toLocalDate.atStartOfDay().`with`(MONDAY)
    val lastDayOfWeek = firstDayOfWeek.plusWeeks(1).minusNanos(1)

    (firstDayOfWeek, lastDayOfWeek)
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
