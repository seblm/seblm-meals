package meals.domain

import java.time.LocalDateTime

import scala.concurrent.Future

trait MealsService {

  def currentWeekMeals(): Future[WeekMeals]

  def nextWeekMeals(): Future[WeekMeals]

  def shuffle(day: LocalDateTime): Future[Option[Meal]]

  def shuffleAll(): Future[WeekMeals]

  def delete(mealTime: LocalDateTime): Future[Unit]

}
