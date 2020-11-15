package meals

import java.time.DayOfWeek._
import java.time._
import java.util.UUID

import meals.domain.{Meal, MealRepository, MealsService, MealsServiceImpl, WeekMeals}
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
      mealRepository.all() returns MealsServiceSpec.AllResponse(
        Seq(
          "monday next week                          ->                   2020-03-02T20:00",
          "monday current week and tuesday next week -> 2020-02-24T20:00, 2020-03-03T20:00",
          "tuesday current week                      -> 2020-02-25T20:00",
          "wednesday current week                    -> 2020-02-26T20:00"
        )
      )
      mealRepository.link(*, eqTo(day)) answers { (mealRow: MealRow, _: LocalDateTime) =>
        Future.successful(Meal(DayOfWeek.TUESDAY, mealRow.description))
      }

      whenReady(mealsService.shuffle(day)) { meal =>
        meal.value.meal should (be("tuesday current week") or be("wednesday current week"))
      }
  }

  it should "randomly choose all empty meals for a week" in withRepositoryAndService {
    (mealRepository: MealRepository, mealsService: MealsService) =>
      mealRepository.all() returns MealsServiceSpec.AllResponse(
        Seq(
          "monday        -> 2020-02-17T20:00",
          "tuesday       -> 2020-02-18T20:00",
          "wednesday     -> 2020-02-19T20:00",
          "thursday      -> 2020-02-20T20:00",
          "friday        -> 2020-02-21T20:00",
          "saturday      -> 2020-02-22T20:00",
          "sunday        -> 2020-02-23T20:00",
          "previous week -> 2020-02-24T20:00, 2020-02-25T20:00, 2020-02-26T20:00, 2020-02-27T20:00, 2020-02-28T20:00, 2020-02-29T20:00, 2020-03-01T20:00"
        )
      )

      whenReady(mealsService.shuffleAll(ZonedDateTime.parse("2020-03-02T20:00:00+01:00[Europe/Paris]"))) { weekMeals =>
        WeekMeals
          .allWeekMeals(weekMeals)
          .flatMap(
            _.meal
          ) should contain only ("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday")
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

object MealsServiceSpec {

  case class AllResponse(all: Seq[String])

  private implicit def toAllResponse(allResponse: AllResponse): Future[Map[MealRow, Seq[LocalDateTime]]] =
    Future.successful(toAllResponseMap(allResponse))

  private def toAllResponseMap(allResponse: AllResponse): Map[MealRow, Seq[LocalDateTime]] = allResponse.all match {
    case Nil          => Map.empty
    case last :: Nil  => Map(toAllResponseMap(last))
    case head :: tail => Map(toAllResponseMap(head)) ++ toAllResponseMap(AllResponse(tail))
  }

  private def toAllResponseMap(line: String): (MealRow, Seq[LocalDateTime]) = line.split("->") match {
    case Array(description, dates) =>
      MealRow(UUID.randomUUID(), description.trim()) -> dates.split(",").map(x => LocalDateTime.parse(x.trim))
  }

}
