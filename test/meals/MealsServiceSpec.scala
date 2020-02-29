package meals

import java.time.{Clock, Instant, LocalDateTime, ZoneId}
import java.time.DayOfWeek._

import meals.domain.{Meal, MealRepository, MealsService, MealsServiceImpl}
import org.mockito.scalatest.IdiomaticMockito
import org.scalatest.OptionValues._
import org.scalatest.concurrent.ScalaFutures.whenReady
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.global
import scala.concurrent.{ExecutionContext, Future}

class MealsServiceSpec extends FlatSpec with Matchers with IdiomaticMockito {

  implicit val ec: ExecutionContext = global

  "Meals" should "be displayed for each day of current week" in withRepositoryAndService {
    (mealRepository: MealRepository, mealsService: MealsService) =>
      mealRepository.meals(
        LocalDateTime.parse("2020-02-24T00:00"),
        LocalDateTime.parse("2020-03-01T23:59:59.999999999")
      ) returns Future.successful(
        Seq(
          Meal(THURSDAY, "saucisson brioché salade"),
          Meal(FRIDAY, "chou-fleur pomme de terre lardons")
        )
      )

      whenReady(mealsService.currentWeekMeals()) { weekMeals =>
        weekMeals.monday should not be defined
        weekMeals.tuesday should not be defined
        weekMeals.wednesday should not be defined
        weekMeals.thursday.value shouldBe "saucisson brioché salade"
        weekMeals.friday.value shouldBe "chou-fleur pomme de terre lardons"
        weekMeals.saturday should not be defined
        weekMeals.sunday should not be defined
      }
  }

  it should "be displayed for each day of next week" in withRepositoryAndService {
    (mealRepository: MealRepository, mealsService: MealsService) =>
      mealRepository.meals(
        LocalDateTime.parse("2020-03-02T00:00"),
        LocalDateTime.parse("2020-03-08T23:59:59.999999999")
      ) returns Future
        .successful(
          Seq(
            Meal(MONDAY, "galettes de blé noir"),
            Meal(TUESDAY, "chipolatas pâtes"),
            Meal(WEDNESDAY, "ratatouille riz"),
            Meal(SATURDAY, "lentilles saucisses (Morteau, Montbelliard) carottes"),
            Meal(SUNDAY, "quenelles riz sauce tomate")
          )
        )

      whenReady(mealsService.nextWeekMeals()) { weekMeals =>
        weekMeals.monday.value shouldBe "galettes de blé noir"
        weekMeals.tuesday.value shouldBe "chipolatas pâtes"
        weekMeals.wednesday.value shouldBe "ratatouille riz"
        weekMeals.thursday should not be defined
        weekMeals.friday should not be defined
        weekMeals.saturday.value shouldBe "lentilles saucisses (Morteau, Montbelliard) carottes"
        weekMeals.sunday.value shouldBe "quenelles riz sauce tomate"
      }
  }

  private def withRepositoryAndService(testCode: (MealRepository, MealsService) => Any): Any = {
    val mealRepository = mock[MealRepository]
    val mealsService = new MealsServiceImpl(
      clock = Clock.fixed(Instant.parse("2020-02-29T02:22:00Z"), ZoneId.of("Europe/Paris")),
      repository = mealRepository
    )

    testCode(mealRepository, mealsService)
  }

}
