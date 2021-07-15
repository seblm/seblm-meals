package meals.domain

import java.time.DayOfWeek._
import java.time.LocalDateTime

object MealTimeConverter:

  def displayTime(time: LocalDateTime): String = (time.getDayOfWeek, time.getHour) match {
    case (MONDAY, 12)    => "lundi midi"
    case (MONDAY, 20)    => "lundi soir"
    case (TUESDAY, 12)   => "mardi midi"
    case (TUESDAY, 20)   => "mardi soir"
    case (WEDNESDAY, 12) => "mercredi midi"
    case (WEDNESDAY, 20) => "mercredi soir"
    case (THURSDAY, 12)  => "jeudi midi"
    case (THURSDAY, 20)  => "jeudi soir"
    case (FRIDAY, 12)    => "vendredi midi"
    case (FRIDAY, 20)    => "vendredi soir"
    case (SATURDAY, 12)  => "samedi midi"
    case (SATURDAY, 20)  => "samedi soir"
    case (SUNDAY, 12)    => "dimanche midi"
    case (SUNDAY, 20)    => "dimanche soir"
    case _               => "?"
  }
