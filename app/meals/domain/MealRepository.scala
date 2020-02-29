package meals.domain

import java.time.LocalDateTime

import scala.concurrent.Future

trait MealRepository {

  def meals(from: LocalDateTime, to: LocalDateTime): Future[Seq[Meal]]

}
