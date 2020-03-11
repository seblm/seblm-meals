package meals

import java.time.DayOfWeek._
import java.time._
import java.util.UUID

import meals.domain.{Meal, MealRepository, MealsService, MealsServiceImpl}
import meals.infrastructure.MealRow
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
        weekMeals.monday.meal should not be defined
        weekMeals.tuesday.meal should not be defined
        weekMeals.wednesday.meal should not be defined
        weekMeals.thursday.meal.value shouldBe "saucisson brioché salade"
        weekMeals.friday.meal.value shouldBe "chou-fleur pomme de terre lardons"
        weekMeals.saturday.meal should not be defined
        weekMeals.sunday.meal should not be defined
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
        weekMeals.monday.meal.value shouldBe "galettes de blé noir"
        weekMeals.tuesday.meal.value shouldBe "chipolatas pâtes"
        weekMeals.wednesday.meal.value shouldBe "ratatouille riz"
        weekMeals.thursday.meal should not be defined
        weekMeals.friday.meal should not be defined
        weekMeals.saturday.meal.value shouldBe "lentilles saucisses (Morteau, Montbelliard) carottes"
        weekMeals.sunday.meal.value shouldBe "quenelles riz sauce tomate"
      }
  }

  it should "randomly choose a meal" in withRepositoryAndService {
    (mealRepository: MealRepository, mealsService: MealsService) =>
      val day = LocalDateTime.parse("2020-03-02T20:00")
      mealRepository.all() returns Future.successful(
        Map(
          MealRow(UUID.randomUUID(), "monday next week") -> Seq(LocalDateTime.parse("2020-03-02T20:00")),
          MealRow(UUID.randomUUID(), "monday current week and tuesday next week") -> Seq(
            LocalDateTime.parse("2020-02-24T20:00"),
            LocalDateTime.parse("2020-03-03T20:00")
          ),
          MealRow(UUID.randomUUID(), "tuesday current week") -> Seq(LocalDateTime.parse("2020-02-25T20:00")),
          MealRow(UUID.randomUUID(), "wednesday current week") -> Seq(LocalDateTime.parse("2020-02-26T20:00"))
        )
      )
      mealRepository.link(*, eqTo(day)) answers { (mealRow: MealRow, _: LocalDateTime) =>
        Future.successful(Meal(DayOfWeek.TUESDAY, mealRow.description))
      }

      whenReady(mealsService.shuffle(day)) { meal =>
        meal.meal should (be("tuesday current week") or be("wednesday current week"))
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
