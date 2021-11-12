package meals.domain

import java.time.{LocalDateTime, Year}
import java.util.UUID
import scala.concurrent.Future

trait MealsService:

  def meal(id: UUID): Future[Option[MealByTimes]]

  def meals(year: Year, week: Int): Future[WeekMeals]

  def linkOrInsert(mealTime: LocalDateTime, mealDescription: String): Future[Meal]

  def shuffle(day: LocalDateTime): Future[Option[Meal]]

  def suggest(reference: LocalDateTime, search: Option[String]): Future[SuggestResponse]

  def delete(mealTime: LocalDateTime): Future[Unit]
