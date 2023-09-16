package meals

import meals.domain.MealRepository
import meals.infrastructure.MealRow
import org.scalatest.concurrent.Eventually.eventually
import org.scalatest.concurrent.ScalaFutures.whenReady

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.ExecutionContext

class MealsDAOSpec extends MealsPlaySpec {

  "MealsDAO" must {
    "insert a new meal" in {
      val mealsDAO: MealRepository = mealsComponents.mealRepository
      val row = MealRow(UUID.randomUUID(), "some meal")
      val time = LocalDateTime.parse("2020-01-05T12:00:00")
      implicit val executionContext: ExecutionContext = app.actorSystem.dispatcher

      val inserts = for {
        _ <- mealsDAO.insert(row)
        insertedMeal <- mealsDAO.link(row, time)
      } yield insertedMeal

      eventually {
        inserts.value.value mustBe Symbol("Success")
      }

      whenReady(mealsDAO.all()) { allMeals =>
        allMeals must contain only (row -> Seq(time))
      }
    }
  }

}
