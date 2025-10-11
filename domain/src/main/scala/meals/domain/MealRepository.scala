package meals.domain

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.Future

trait MealRepository:

  def meals(id: UUID): Future[Seq[MealEntry]]

  def meals(from: LocalDateTime, to: LocalDateTime): Future[Seq[MealEntry]]

  def linkOrInsert(mealTime: LocalDateTime, mealDescription: String): Future[MealEntry]

  def unlink(at: LocalDateTime): Future[Unit]

  def all(): Future[Map[Meal, Seq[LocalDateTime]]]
