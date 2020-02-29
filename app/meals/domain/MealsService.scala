package meals.domain

import scala.concurrent.Future

trait MealsService {

  def currentWeekMeals(): Future[Seq[Meal]]

  def nextWeekMeals(): Future[Seq[Meal]]

}
