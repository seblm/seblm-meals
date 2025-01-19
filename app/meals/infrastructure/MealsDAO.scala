package meals.infrastructure

import meals.domain.{Meal, MealRepository}
import play.api.db.slick.HasDatabaseConfig
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import java.time.LocalDateTime
import java.util.UUID
import scala.Function.const
import scala.concurrent.{ExecutionContext, Future}

class MealsDAO(dbConfig1: DatabaseConfig[JdbcProfile])(using ExecutionContext)
    extends HasDatabaseConfig[JdbcProfile],
      MealRepository:

  protected override lazy val dbConfig: DatabaseConfig[JdbcProfile] = dbConfig1

  import profile.api.*

  private val meals = TableQuery[MealsTable]

  private def insert(meal: MealRow): Future[Unit] = db.run(meals += meal).map(const(()))

  class MealsTable(tag: Tag) extends Table[MealRow](tag, "meals"):

    def id = column[UUID]("id", O.PrimaryKey)

    def description = column[String]("description")

    def url = column[Option[String]]("url")

    def * = (id, description, url) <> (
      (id, description, url) => MealRow(id, description, url),
      mealRow => Some((mealRow.id, mealRow.description, mealRow.url))
    )

  private val meals_by_time = TableQuery[MealsByTimeTable]

  extension [C[_]](q: Query[MealsByTimeTable, MealsByTimeRow, C])
    def withMeal: Query[(MealsByTimeTable, MealsTable), (MealsByTimeRow, MealRow), C] =
      q.join(meals).on(_.mealId === _.id)

  private def toMeal(mealsByTime: MealsByTimeRow, meal: MealRow): Meal =
    Meal(mealsByTime.mealId, mealsByTime.time, meal.description, meal.url)

  override def meals(id: UUID): Future[Seq[Meal]] =
    db.run(meals_by_time.filter(meal => meal.mealId === id).withMeal.sortBy(_._1.time.desc).result)
      .map(_.map(toMeal.tupled))

  override def meals(from: LocalDateTime, to: LocalDateTime): Future[Seq[Meal]] =
    db.run(meals_by_time.filter(meal => meal.time > from && meal.time < to).withMeal.sortBy(_._1.time.asc).result)
      .map(_.map(toMeal.tupled))

  override def all(): Future[Map[MealRow, Seq[LocalDateTime]]] =
    db.run(meals_by_time.withMeal.result)
      .map(_.foldLeft(Map.empty[MealRow, Seq[LocalDateTime]]):
        case (acc, (mealsByType, mealRow)) =>
          acc.updated(mealRow, acc.getOrElse(mealRow, Seq.empty[LocalDateTime]) :+ mealsByType.time))

  private def link(meal: MealRow, at: LocalDateTime): Future[Meal] =
    db.run(meals_by_time.insertOrUpdate(MealsByTimeRow(at, meal.id)))
      .flatMap:
        case 1 => Future.successful(Meal(meal.id, at, meal.description, meal.url))
        case _ => Future.failed(new Exception("Error when link meal"))

  override def linkOrInsert(mealTime: LocalDateTime, mealDescription: String): Future[Meal] =
    for
      existingMealRow <- db.run(meals.filter(_.description === mealDescription).take(1).result.headOption)
      mealRow <- existingMealRow.fold {
        val newMealRow = MealRow(UUID.randomUUID(), mealDescription, None)
        insert(newMealRow).map(const(newMealRow))
      }(Future.successful)
      newMeal <- link(mealRow, mealTime)
    yield newMeal

  override def unlink(at: LocalDateTime): Future[Unit] =
    db.run(meals_by_time.filter(meal => meal.time === at).delete)
      .flatMap:
        case 1 => Future.successful(())
        case _ => Future.failed(new Exception("Error when unlink meal"))

  class MealsByTimeTable(tag: Tag) extends Table[MealsByTimeRow](tag, "meals_by_time"):

    def time = column[LocalDateTime]("time", O.PrimaryKey)

    def mealId = column[UUID]("meal_id")

    def * = (time, mealId) <> (MealsByTimeRow.apply.tupled, MealsByTimeRow.unapply)

    def meal = foreignKey("meal_fk", mealId, meals)(_.id)
