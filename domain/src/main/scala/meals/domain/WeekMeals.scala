package meals.domain

import java.time.DayOfWeek._
import java.time.format.DateTimeFormatter
import java.time.{Clock, LocalDate, LocalDateTime, Year}
import scala.collection.immutable.ListMap

case class WeekMeals(
    titles: Titles,
    previous: WeekReference,
    now: WeekReference,
    next: WeekReference,
    monday: WeekDay,
    tuesday: WeekDay,
    wednesday: WeekDay,
    thursday: WeekDay,
    friday: WeekDay,
    saturday: WeekDay,
    sunday: WeekDay
):

  def allByDate: Map[LocalDateTime, Option[Meal]] = ListMap(
    monday.reference.atTime(12, 0) -> monday.lunch,
    monday.reference.atTime(20, 0) -> monday.dinner,
    tuesday.reference.atTime(12, 0) -> tuesday.lunch,
    tuesday.reference.atTime(20, 0) -> tuesday.dinner,
    wednesday.reference.atTime(12, 0) -> wednesday.lunch,
    wednesday.reference.atTime(20, 0) -> wednesday.dinner,
    thursday.reference.atTime(12, 0) -> thursday.lunch,
    thursday.reference.atTime(20, 0) -> thursday.dinner,
    friday.reference.atTime(12, 0) -> friday.lunch,
    friday.reference.atTime(20, 0) -> friday.dinner,
    saturday.reference.atTime(12, 0) -> saturday.lunch,
    saturday.reference.atTime(20, 0) -> saturday.dinner,
    sunday.reference.atTime(12, 0) -> sunday.lunch,
    sunday.reference.atTime(20, 0) -> sunday.dinner
  )

case class WeekDay(reference: LocalDate, lunch: Option[Meal], dinner: Option[Meal])

object WeekMeals:

  def apply(year: Year, week: Int, clock: Clock): WeekMeals =
    val now = LocalDateTime.now(clock)
    val (yearPrevious, weekPrevious) = DatesTransformations.yearWeek(now.minusWeeks(1))
    val (yearNow, weekNow) = DatesTransformations.yearWeek(now)
    val (yearNext, weekNext) = DatesTransformations.yearWeek(now.plusWeeks(1))
    val (from, to) = DatesTransformations.range(year, week)
    val reference = from.toLocalDate
    val short = f"${year.getValue} semaine n°$week%02d"
    val fromMonth = Option.when(from.getMonth != to.getMonth)(s" ${DateTimeFormatter.ofPattern("MMMM").format(from)}")
    val fromYear = Option.when(from.getYear != to.getYear)(s" ${DateTimeFormatter.ofPattern("YYYY").format(from)}")
    val long = f"Semaine n°$week%02d - du lundi ${from.getDayOfMonth}${fromMonth
      .getOrElse("")}${fromYear.getOrElse("")} au dimanche ${DateTimeFormatter.ofPattern("d MMMM YYYY").format(to)}"
    WeekMeals(
      titles = Titles(short, long),
      previous = WeekReference(yearPrevious, weekPrevious, yearPrevious == year && weekPrevious == week),
      now = WeekReference(yearNow, weekNow, yearNow == year && weekNow == week),
      next = WeekReference(yearNext, weekNext, yearNext == year && weekNext == week),
      monday = WeekDay(reference.`with`(MONDAY), None, None),
      tuesday = WeekDay(reference.`with`(TUESDAY), None, None),
      wednesday = WeekDay(reference.`with`(WEDNESDAY), None, None),
      thursday = WeekDay(reference.`with`(THURSDAY), None, None),
      friday = WeekDay(reference.`with`(FRIDAY), None, None),
      saturday = WeekDay(reference.`with`(SATURDAY), None, None),
      sunday = WeekDay(reference.`with`(SUNDAY), None, None)
    )

  def allWeekMeals(weekMeals: WeekMeals): Seq[Meal] =
    Seq(
      weekMeals.monday.lunch,
      weekMeals.monday.dinner,
      weekMeals.tuesday.lunch,
      weekMeals.tuesday.dinner,
      weekMeals.wednesday.lunch,
      weekMeals.wednesday.dinner,
      weekMeals.thursday.lunch,
      weekMeals.thursday.dinner,
      weekMeals.friday.lunch,
      weekMeals.friday.dinner,
      weekMeals.saturday.lunch,
      weekMeals.saturday.dinner,
      weekMeals.sunday.lunch,
      weekMeals.sunday.dinner
    ).flatten
