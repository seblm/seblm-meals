package meals.domain

import java.time.LocalDateTime

import meals.infrastructure.MealRow

import scala.concurrent.Future

trait MealRepository {

  def meals(from: LocalDateTime, to: LocalDateTime): Future[Seq[Meal]]

  def insert(meal: MealRow): Future[Unit]

  def link(meal: MealRow, at: LocalDateTime): Future[Meal]

  def unlink(at: LocalDateTime): Future[Unit]

  def all(): Future[Map[MealRow, Seq[LocalDateTime]]]

}
