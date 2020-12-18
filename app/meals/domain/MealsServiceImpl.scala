package meals.domain

import meals.infrastructure.MealRow

import java.time.DayOfWeek._
import java.time._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Random, Try}

class MealsServiceImpl(clock: Clock, repository: MealRepository)(implicit ec: ExecutionContext) extends MealsService {

  override def currentWeekMeals(): Future[WeekMeals] = {
    val now = clock.instant().atZone(ZoneId.of("Europe/Paris"))
    meals(now)(repository).map(mealsByWeekDay(now.toLocalDate))
  }

  override def linkOrInsert(mealTime: LocalDateTime, mealDescription: String): Future[Meal] =
    repository.linkOrInsert(mealTime, mealDescription)

  override def nextWeekMeals(): Future[WeekMeals] = {
    val nowNextWeek = clock.instant().atZone(ZoneId.of("Europe/Paris")).plusWeeks(1)
    meals(nowNextWeek)(repository).map(mealsByWeekDay(nowNextWeek.toLocalDate))
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

  override def shuffleAll(): Future[WeekMeals] = {
    val nowNextWeek = clock.instant().atZone(ZoneId.of("Europe/Paris")).plusWeeks(1)
    val (firstDayOfWeek, lastDayOfWeek) = firstAndLastDayOfWeek(nowNextWeek)
    val until = firstDayOfWeek.minusWeeks(1).minusNanos(1)
    val firstDay = firstDayOfWeek.toLocalDate

    for {
      all <- repository.all()
    } yield {
      val dateByCurrentMeals =
        all.filter(_._2.exists(date => date.isAfter(firstDayOfWeek) && date.isBefore(lastDayOfWeek)))
      val currentMeals = dateByCurrentMeals.keys.toSeq
      val previousMeals = Random.shuffle(
        all
          .filterNot(_._2.exists(_.isAfter(until)))
          .keys
          .filterNot(currentMeals.contains)
          .toSeq
          .take(7 * 2 - currentMeals.length)
      )
      def weekDayWithMeals: (LocalDate, Int) => (WeekDay, Int) = weekDay(dateByCurrentMeals, previousMeals)(_, _)
      val (mondayWeekDay, mondayIndex) = weekDayWithMeals(firstDay, 0)
      val (tuesdayWeekDay, tuesdayIndex) = weekDayWithMeals(firstDay.plusDays(1), mondayIndex)
      val (wednesdayWeekDay, wednesdayIndex) = weekDayWithMeals(firstDay.plusDays(2), tuesdayIndex)
      val (thursdayWeekDay, thursdayIndex) = weekDayWithMeals(firstDay.plusDays(3), wednesdayIndex)
      val (fridayWeekDay, fridayIndex) = weekDayWithMeals(firstDay.plusDays(4), thursdayIndex)
      val (saturdayWeekDay, saturdayIndex) = weekDayWithMeals(firstDay.plusDays(5), fridayIndex)
      val (sundayWeekDay, _) = weekDayWithMeals(firstDay.plusDays(6), saturdayIndex)
      WeekMeals(
        monday = mondayWeekDay,
        tuesday = tuesdayWeekDay,
        wednesday = wednesdayWeekDay,
        thursday = thursdayWeekDay,
        friday = fridayWeekDay,
        saturday = saturdayWeekDay,
        sunday = sundayWeekDay
      )

    }
  }

  private def moreUsedThenMoreRecent(
      first: (MealRow, Seq[LocalDateTime]),
      second: (MealRow, Seq[LocalDateTime])
  ): Boolean =
    if (first._2.length != second._2.length) first._2.length > second._2.length
    else first._2.max.isAfter(second._2.max)

  override def suggest(): Future[Seq[MealSuggest]] =
    repository.all().map { mealsByDate =>
      mealsByDate.toSeq.sortWith(moreUsedThenMoreRecent).take(10).map { case (meal, dates) =>
        MealSuggest(dates.length, meal.description, Duration.between(dates.max, LocalDateTime.now()).toDays.toInt)
      }
    }

  override def delete(mealTime: LocalDateTime): Future[Unit] = repository.unlink(mealTime)

  private def weekDay(
      dateByCurrentMeals: Map[MealRow, Seq[LocalDateTime]],
      previousMeals: Seq[MealRow]
  )(reference: LocalDate, index: Int): (WeekDay, Int) = {
    val noon = reference.atTime(12, 0)
    val evening = reference.atTime(20, 0)
    var mutableIndex = index
    val weekDay = WeekDay(
      reference,
      dateByCurrentMeals
        .find(_._2.exists(_ == noon))
        .map(_._1.description)
        .orElse {
          mutableIndex += 1
          val meal = previousMeals.lift(mutableIndex - 1)
          meal.foreach(meal => repository.link(meal, noon))
          meal.map(_.description)
        }
        .map(description => Meal(noon, description)),
      dateByCurrentMeals
        .find(_._2.exists(_ == evening))
        .map(_._1.description)
        .orElse {
          mutableIndex += 1
          val meal = previousMeals.lift(mutableIndex - 1)
          meal.foreach(meal => repository.link(meal, evening))
          meal.map(_.description)
        }
        .map(description => Meal(evening, description))
    )
    (weekDay, mutableIndex)
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

  private def mealsByWeekDay(reference: LocalDate)(meals: Seq[Meal]): WeekMeals =
    meals.foldLeft(WeekMeals(reference))((weekMeals: WeekMeals, meal: Meal) =>
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
