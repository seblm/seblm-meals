package meals

import java.time._
import java.util.UUID

import meals.domain._
import meals.infrastructure.MealRow
import org.mockito.scalatest.IdiomaticMockito
import org.scalatest.OptionValues._
import org.scalatest.concurrent.ScalaFutures.whenReady
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._

import scala.concurrent.ExecutionContext.global
import scala.concurrent.{ExecutionContext, Future}

class MealsServiceSpec extends AnyFlatSpec with IdiomaticMockito {

  implicit val ec: ExecutionContext = global

  "Meals" should "be displayed for each day of current week" in withRepositoryAndService {
    (mealRepository: MealRepository, mealsService: MealsService) =>
      mealRepository.meals(
        LocalDateTime.parse("2020-02-24T00:00"),
        LocalDateTime.parse("2020-03-01T23:59:59.999999999")
      ) returns Future.successful(
        Seq(
          Meal(LocalDateTime.parse("2020-02-27T12:00"), "saucisson brioché salade"),
          Meal(LocalDateTime.parse("2020-02-28T20:00"), "chou-fleur pomme de terre lardons")
        )
      )

      whenReady(mealsService.currentWeekMeals()) { weekMeals =>
        weekMeals.monday.lunch should not be defined
        weekMeals.monday.dinner should not be defined
        weekMeals.tuesday.lunch should not be defined
        weekMeals.tuesday.dinner should not be defined
        weekMeals.wednesday.lunch should not be defined
        weekMeals.wednesday.dinner should not be defined
        weekMeals.thursday.lunch.value.meal shouldBe "saucisson brioché salade"
        weekMeals.thursday.dinner should not be defined
        weekMeals.friday.lunch should not be defined
        weekMeals.friday.dinner.value.meal shouldBe "chou-fleur pomme de terre lardons"
        weekMeals.saturday.lunch should not be defined
        weekMeals.saturday.dinner should not be defined
        weekMeals.sunday.lunch should not be defined
        weekMeals.sunday.dinner should not be defined
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
            Meal(LocalDateTime.parse("2020-03-02T12:00"), "galettes de blé noir"),
            Meal(LocalDateTime.parse("2020-03-03T20:00"), "chipolatas pâtes"),
            Meal(LocalDateTime.parse("2020-03-04T20:00"), "ratatouille riz"),
            Meal(LocalDateTime.parse("2020-03-07T12:00"), "lentilles saucisses (Morteau, Montbelliard) carottes"),
            Meal(LocalDateTime.parse("2020-03-08T12:00"), "quenelles riz sauce tomate")
          )
        )

      whenReady(mealsService.nextWeekMeals()) { weekMeals =>
        weekMeals.monday.lunch.value.meal shouldBe "galettes de blé noir"
        weekMeals.monday.dinner should not be defined
        weekMeals.tuesday.lunch should not be defined
        weekMeals.tuesday.dinner.value.meal shouldBe "chipolatas pâtes"
        weekMeals.wednesday.lunch should not be defined
        weekMeals.wednesday.dinner.value.meal shouldBe "ratatouille riz"
        weekMeals.thursday.lunch should not be defined
        weekMeals.thursday.dinner should not be defined
        weekMeals.friday.lunch should not be defined
        weekMeals.friday.dinner should not be defined
        weekMeals.saturday.lunch.value.meal shouldBe "lentilles saucisses (Morteau, Montbelliard) carottes"
        weekMeals.saturday.dinner should not be defined
        weekMeals.sunday.lunch.value.meal shouldBe "quenelles riz sauce tomate"
        weekMeals.sunday.dinner should not be defined
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
        Future.successful(Meal(day, mealRow.description))
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
          .map(
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
      MealRow(UUID.randomUUID(), description.trim()) -> dates
        .split(",")
        .toIndexedSeq
        .map(x => LocalDateTime.parse(x.trim))
  }

}
