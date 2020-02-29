package meals.domain

import scala.concurrent.Future

trait MealsService {

  def currentWeekMeals(): Future[WeekMeals]

  def nextWeekMeals(): Future[WeekMeals]

}
