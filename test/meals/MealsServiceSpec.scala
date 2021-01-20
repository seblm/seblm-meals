package meals

import meals.domain.MealTime.Lunch
import meals.domain._
import meals.infrastructure.MealRow
import org.mockito.scalatest.IdiomaticMockito
import org.scalatest.OptionValues._
import org.scalatest.concurrent.ScalaFutures.whenReady
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.contain
import org.scalatest.matchers.should.Matchers._

import java.time._
import java.time.format.DateTimeFormatter
import java.util.UUID
import scala.concurrent.ExecutionContext.global
import scala.concurrent.{ExecutionContext, Future}

class MealsServiceSpec extends AnyFlatSpec with IdiomaticMockito {

  implicit val ec: ExecutionContext = global

  private val monthFormatter = DateTimeFormatter.ofPattern("MMMM")

  "Meals" should "be displayed for each day of current week" in withRepositoryAndService {
    (mealRepository: MealRepository, mealsService: MealsService) =>
      val from = LocalDateTime.parse("2020-02-24T00:00")
      val to = LocalDateTime.parse("2020-03-01T23:59:59.999999999")
      mealRepository.meals(from, to) returns Future.successful(
        Seq(
          Meal(LocalDateTime.parse("2020-02-27T12:00"), "saucisson brioché salade"),
          Meal(LocalDateTime.parse("2020-02-28T20:00"), "chou-fleur pomme de terre lardons")
        )
      )
      val february = monthFormatter.format(from)
      val march = monthFormatter.format(to)

      whenReady(mealsService.meals(Year.of(2020), 9)) { weekMeals =>
        weekMeals.titles.short shouldBe "2020 semaine n°09"
        weekMeals.titles.long shouldBe s"Semaine n°09 - du lundi 24 $february au dimanche 1 $march 2020"
        weekMeals.previous.year shouldBe Year.of(2020)
        weekMeals.previous.week shouldBe 8
        weekMeals.previous.isActive shouldBe false
        weekMeals.now.year shouldBe Year.of(2020)
        weekMeals.now.week shouldBe 9
        weekMeals.now.isActive shouldBe true
        weekMeals.next.year shouldBe Year.of(2020)
        weekMeals.next.week shouldBe 10
        weekMeals.next.isActive shouldBe false
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
      val from = LocalDateTime.parse("2020-03-02T00:00")
      val to = LocalDateTime.parse("2020-03-08T23:59:59.999999999")
      mealRepository.meals(from, to) returns Future
        .successful(
          Seq(
            Meal(LocalDateTime.parse("2020-03-02T12:00"), "galettes de blé noir"),
            Meal(LocalDateTime.parse("2020-03-03T20:00"), "chipolatas pâtes"),
            Meal(LocalDateTime.parse("2020-03-04T20:00"), "ratatouille riz"),
            Meal(LocalDateTime.parse("2020-03-07T12:00"), "lentilles saucisses (Morteau, Montbelliard) carottes"),
            Meal(LocalDateTime.parse("2020-03-08T12:00"), "quenelles riz sauce tomate")
          )
        )

      val march = monthFormatter.format(from)
      whenReady(mealsService.meals(Year.of(2020), 10)) { weekMeals =>
        weekMeals.titles.short shouldBe "2020 semaine n°10"
        weekMeals.titles.long shouldBe s"Semaine n°10 - du lundi 2 au dimanche 8 $march 2020"
        weekMeals.previous.year shouldBe Year.of(2020)
        weekMeals.previous.week shouldBe 8
        weekMeals.previous.isActive shouldBe false
        weekMeals.now.year shouldBe Year.of(2020)
        weekMeals.now.week shouldBe 9
        weekMeals.now.isActive shouldBe false
        weekMeals.next.year shouldBe Year.of(2020)
        weekMeals.next.week shouldBe 10
        weekMeals.next.isActive shouldBe true
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
      mealRepository.all() returns MealsServiceSpec
        .AllResponse(
          Seq(
            "monday next week                          ->                   2020-03-02T20:00",
            "monday current week and tuesday next week -> 2020-02-24T20:00, 2020-03-03T20:00",
            "tuesday current week                      -> 2020-02-25T20:00",
            "wednesday current week                    -> 2020-02-26T20:00"
          )
        )
        .toFuture
      mealRepository.link(*, eqTo(day)) answers { (mealRow: MealRow, _: LocalDateTime) =>
        Future.successful(Meal(day, mealRow.description))
      }

      whenReady(mealsService.shuffle(day)) { meal =>
        meal.value.meal should (be("tuesday current week") or be("wednesday current week"))
      }
  }

  it should "suggest meals ordered by most used then by date" in withRepositoryAndService {
    (mealRepository, mealsService) =>
      mealRepository.all() returns MealsServiceSpec
        .AllResponse(
          Seq(
            "salade tomates concombres -> 2020-02-17T12:00, 2020-02-18T12:00",
            "tomates farcies           -> 2020-02-17T20:00",
            "pâtes sauce tomate        -> 2020-02-19T12:00",
            "ratatouille               -> 2020-02-20T12:00"
          )
        )
        .toFuture

      whenReady(mealsService.suggest(Lunch, None)) { suggests =>
        suggests should contain inOrderOnly (
          MealSuggest(2, "salade tomates concombres", "salade tomates concombres", 10),
          MealSuggest(1, "ratatouille", "ratatouille", 8),
          MealSuggest(1, "pâtes sauce tomate", "pâtes sauce tomate", 9),
        )
      }
  }

  it should "suggest meals filtered by a search" in withRepositoryAndService { (mealRepository, mealsService) =>
    mealRepository.all() returns MealsServiceSpec
      .AllResponse(
        Seq(
          "salade tomates concombres -> 2020-02-17T12:00, 2020-02-18T12:00",
          "tomates farcies           -> 2020-02-17T20:00",
          "pâtes sauce tomate        -> 2020-02-19T12:00",
          "ratatouille               -> 2020-02-20T12:00"
        )
      )
      .toFuture

    whenReady(mealsService.suggest(Lunch, Some("omat"))) { suggests =>
      suggests should contain inOrderOnly (
        MealSuggest(2, "salade tomates concombres", "salade t<strong>omat</strong>es concombres", 10),
        MealSuggest(1, "pâtes sauce tomate", "pâtes sauce t<strong>omat</strong>e", 9),
      )
    }
  }

  it should "highlight search" in withRepositoryAndService { (mealRepository, mealsService) =>
    mealRepository.all() returns MealsServiceSpec
      .AllResponse(
        Seq(
          "salade tomates concombres -> 2020-02-17T12:00",
          "concombres maïs -> 2020-02-18T12:00",
          "salade concombres tomates -> 2020-02-19T12:00"
        )
      )
      .toFuture

    whenReady(mealsService.suggest(Lunch, Some("concombres"))) { suggests =>
      suggests.map(_.descriptionLabel) should contain only (
        "salade tomates <strong>concombres</strong>",
        "<strong>concombres</strong> maïs",
        "salade <strong>concombres</strong> tomates",
      )
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

  implicit class AllResponseOps(allResponse: AllResponse) {

    def toFuture: Future[Map[MealRow, Seq[LocalDateTime]]] =
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

}
