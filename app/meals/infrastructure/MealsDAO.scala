package meals.infrastructure

import java.time.LocalDateTime
import java.util.UUID

import javax.inject.Inject
import meals.domain.{Meal, MealRepository}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{AbstractController, ControllerComponents}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class MealsDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider, cc: ControllerComponents)(
    implicit executionContext: ExecutionContext
) extends AbstractController(cc)
    with HasDatabaseConfigProvider[JdbcProfile]
    with MealRepository {

  import profile.api._

  private val meals = TableQuery[MealsTable]

  def insert(meal: MealRow): Future[Unit] = db.run(meals += meal).map(ignore)

  class MealsTable(tag: Tag) extends Table[MealRow](tag, "meals") {

    def id = column[UUID]("id", O.PrimaryKey)

    def description = column[String]("description")

    def * = (id, description) <> (MealRow.tupled, MealRow.unapply)

  }

  private val meals_by_time = TableQuery[MealsByTimeTable]

  implicit class MealsByTimeExtensions[C[_]](q: Query[MealsByTimeTable, MealsByTimeRow, C]) {
    def withMeal: Query[(MealsByTimeTable, MealsTable), (MealsByTimeRow, MealRow), C] =
      q.join(meals).on(_.mealId === _.id)
  }

  private def toMeal(mealsByTime: MealsByTimeRow, meal: MealRow): Meal =
    Meal(mealsByTime.time.getDayOfWeek, meal.description)

  override def meals(from: LocalDateTime, to: LocalDateTime): Future[Seq[Meal]] =
    db.run(meals_by_time.filter(meal => meal.time > from && meal.time < to).withMeal.sortBy(_._1.time.asc).result)
      .map(_.map((toMeal _).tupled))

  def allMeals(): Future[Seq[Meal]] = db.run(meals_by_time.withMeal.result).map(_.map((toMeal _).tupled))

  def insert(mealByTime: MealsByTimeRow): Future[Unit] = db.run(meals_by_time += mealByTime).map(ignore)

  class MealsByTimeTable(tag: Tag) extends Table[MealsByTimeRow](tag, "meals_by_time") {

    def time = column[LocalDateTime]("time", O.PrimaryKey)

    def mealId = column[UUID]("meal_id")

    def * = (time, mealId) <> (MealsByTimeRow.tupled, MealsByTimeRow.unapply)

    def meal = foreignKey("meal_fk", mealId, meals)(_.id)

  }

  private def ignore(a: Any): Unit = ()

}
