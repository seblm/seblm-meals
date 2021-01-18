package meals.domain

import java.time.DayOfWeek.MONDAY
import java.time.temporal.IsoFields
import java.time.{Clock, LocalDate, LocalDateTime, Year}

object DatesTransformations {

  def yearWeek(clock: Clock): (Year, Int) = {
    val now = clock.instant().atZone(clock.getZone)
    (Year.of(now.get(IsoFields.WEEK_BASED_YEAR)), now.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR))
  }

  def range(year: Year, week: Int): (LocalDateTime, LocalDateTime) = {
    val monday = LocalDate.EPOCH
      .`with`(IsoFields.WEEK_BASED_YEAR, year.getValue)
      .`with`(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week)
      .`with`(MONDAY)
      .atStartOfDay()
    (monday, monday.plusWeeks(1).minusNanos(1))
  }

}
