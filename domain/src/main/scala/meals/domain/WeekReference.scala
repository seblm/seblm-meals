package meals.domain

import java.time.{LocalDateTime, Year}

case class WeekReference(year: Year, week: Int, isActive: Boolean)

object WeekReference:

  def apply(time: LocalDateTime): WeekReference =
    val (year, week) = DatesTransformations.yearWeek(time)
    WeekReference(year = year, week = week, isActive = true)
