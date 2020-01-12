package meals

import java.time.LocalDateTime
import java.util.UUID

import org.scalatest.TestData
import org.scalatest.TryValues._
import org.scalatest.concurrent.ScalaFutures.PatienceConfig
import org.scalatest.time.{Seconds, Span}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import test.utils.FutureHelper.eventually

import scala.concurrent.{ExecutionContext, Future}

class MealsDAOSpec extends PlaySpec with GuiceOneAppPerTest {

  override def newAppForTest(testData: TestData): Application = {
    new GuiceApplicationBuilder().configure(Map(
      "slick.dbs.default.profile" -> "slick.jdbc.H2Profile$",
      "slick.dbs.default.db.driver" -> "org.h2.Driver",
      "slick.dbs.default.db.url" -> "jdbc:h2:mem:meals;DATABASE_TO_UPPER=FALSE",
      "slick.dbs.default.db.numThreads" -> "5",
    )).build()
  }

  "MealsDAO" must {
    "insert a new meal" in {
      val app2dao = Application.instanceCache[MealsDAO]
      val mealsDAO: MealsDAO = app2dao(app)
      val row = MealRow(UUID.randomUUID(), "some meal")
      implicit val patienceConfig: PatienceConfig = PatienceConfig(Span(30, Seconds), Span(1, Seconds))
      implicit val executionContext: ExecutionContext = app.actorSystem.dispatcher

      val inserts = eventually(Future.sequence(Iterable(
        mealsDAO.insert(row),
        mealsDAO.insert(MealsByTimeRow(LocalDateTime.parse("2020-01-05T12:00:00"), row.id))
      )))

      inserts mustBe Symbol("success")
      val allMeals = eventually(mealsDAO.allMeals())
      allMeals.success.value must contain only Meal("2020-01-05T12:00", "some meal")
    }
  }

}
