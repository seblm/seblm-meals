package meals

import meals.domain.MealRepository
import meals.infrastructure.MealRow
import org.scalatest.concurrent.Eventually
import org.scalatest.concurrent.ScalaFutures.whenReady
import org.scalatest.time.{Millis, Span}

import java.time.LocalDateTime

class MealsDAOSpec extends MealsPlaySpec with Eventually:

  implicit override val patienceConfig: PatienceConfig = PatienceConfig(timeout = scaled(Span(300, Millis)))

  "MealsDAO" must {
    "insert a new meal" in {
      val mealsDAO: MealRepository = mealsComponents.mealRepository
      val time = LocalDateTime.parse("2020-01-05T12:00:00")

      val inserts = mealsDAO.linkOrInsert(time, "some meal")

      eventually {
        inserts.value.value must be(Symbol("Success"))
      }

      whenReady(mealsDAO.all()) { (allMeals: Map[MealRow, Seq[LocalDateTime]]) =>
        allMeals.values must contain only Seq(time)
      }
    }
  }
