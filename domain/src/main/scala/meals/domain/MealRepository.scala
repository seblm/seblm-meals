package meals.domain

import meals.infrastructure.MealRow

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.Future

trait MealRepository:

  def meals(id: UUID): Future[Seq[Meal]]

  def meals(from: LocalDateTime, to: LocalDateTime): Future[Seq[Meal]]

  def linkOrInsert(mealTime: LocalDateTime, mealDescription: String): Future[Meal]

  def unlink(at: LocalDateTime): Future[Unit]

  def all(): Future[Map[MealRow, Seq[LocalDateTime]]]
