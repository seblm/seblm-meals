package meals

import meals.infrastructure.{MealRow, MealsDAO}
import org.scalatest.TestData
import org.scalatest.concurrent.Eventually.eventually
import org.scalatest.concurrent.ScalaFutures.whenReady
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.ExecutionContext

class MealsDAOSpec extends PlaySpec with GuiceOneAppPerTest {

  override def newAppForTest(testData: TestData): Application = {
    new GuiceApplicationBuilder()
      .configure(
        Map(
          "slick.dbs.default.profile" -> "slick.jdbc.H2Profile$",
          "slick.dbs.default.db.driver" -> "org.h2.Driver",
          "slick.dbs.default.db.url" -> "jdbc:h2:mem:meals;DATABASE_TO_UPPER=FALSE",
          "slick.dbs.default.db.numThreads" -> "5"
        )
      )
      .build()
  }

  "MealsDAO" must {
    "insert a new meal" in {
      val app2dao = Application.instanceCache[MealsDAO]
      val mealsDAO: MealsDAO = app2dao(app)
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
