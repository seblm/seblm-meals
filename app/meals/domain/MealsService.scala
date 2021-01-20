package meals.domain

import java.time.{LocalDateTime, Year}
import scala.concurrent.Future

trait MealsService {

  def meals(year: Year, week: Int): Future[WeekMeals]

  def linkOrInsert(mealTime: LocalDateTime, mealDescription: String): Future[Meal]

  def shuffle(day: LocalDateTime): Future[Option[Meal]]

  def suggest(mealTime: MealTime, search: Option[String]): Future[Seq[MealSuggest]]

  def delete(mealTime: LocalDateTime): Future[Unit]

}
