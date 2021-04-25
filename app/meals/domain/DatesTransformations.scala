package meals.domain

import java.time.DayOfWeek.MONDAY
import java.time.temporal.IsoFields.{WEEK_BASED_YEAR, WEEK_OF_WEEK_BASED_YEAR}
import java.time.{LocalDate, LocalDateTime, Year}

object DatesTransformations {

  def yearWeek(from: LocalDateTime): (Year, Int) =
    (Year.of(from.get(WEEK_BASED_YEAR)), from.get(WEEK_OF_WEEK_BASED_YEAR))

  def range(year: Year, week: Int): (LocalDateTime, LocalDateTime) = {
    val monday = LocalDate.EPOCH
      .`with`(WEEK_BASED_YEAR, year.getValue)
      .`with`(WEEK_OF_WEEK_BASED_YEAR, week)
      .`with`(MONDAY)
      .atStartOfDay()
    (monday, monday.plusWeeks(1).minusNanos(1))
  }

  def score(reference: LocalDateTime, date: LocalDateTime): Long = {
    val daysUntilSameYear =
      Math.abs(date.withYear(reference.getYear).toLocalDate.toEpochDay - reference.toLocalDate.toEpochDay)
    val daysUntil = Math.abs(date.toLocalDate.toEpochDay - reference.toLocalDate.toEpochDay)
    if (daysUntil < 7) 0 else Math.abs(183 - daysUntilSameYear)
  }

}
