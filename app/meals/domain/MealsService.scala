package meals.domain

import java.time.LocalDateTime
import scala.concurrent.Future

trait MealsService {

  def currentWeekMeals(): Future[WeekMeals]

  def linkOrInsert(mealTime: LocalDateTime, mealDescription: String): Future[Meal]

  def nextWeekMeals(): Future[WeekMeals]

  def shuffle(day: LocalDateTime): Future[Option[Meal]]

  def shuffleAll(): Future[WeekMeals]

  def suggest(): Future[Seq[MealSuggest]]

  def delete(mealTime: LocalDateTime): Future[Unit]

}
