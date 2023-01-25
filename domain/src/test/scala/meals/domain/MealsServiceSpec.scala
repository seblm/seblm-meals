package meals.domain

import meals.domain.*
import meals.infrastructure.MealRow
import org.mockito.AdditionalAnswers.answer
import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.{mock, when}
import org.scalatest.OptionValues.*
import org.scalatest.concurrent.ScalaFutures.whenReady
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.contain
import org.scalatest.matchers.should.Matchers.*

import java.time.*
import java.time.format.DateTimeFormatter
import java.util.UUID
import scala.concurrent.ExecutionContext.global
import scala.concurrent.{ExecutionContext, Future}

class MealsServiceSpec extends AnyFlatSpec {

  implicit val ec: ExecutionContext = global

  private val monthFormatter = DateTimeFormatter.ofPattern("MMMM")

  "Meals" should "be displayed for each day of current week" in new WithRepositoryAndService() {
    private val from = LocalDateTime.parse("2020-02-24T00:00")
    private val to = LocalDateTime.parse("2020-03-01T23:59:59.999999999")
    when(mealRepository.meals(from, to)).thenReturn(
      Future.successful(
        Seq(
          Meal(
            id = UUID.fromString("8ffaf9c5-f9a4-4d7d-8358-d65f17882a2a"),
            time = LocalDateTime.parse("2020-02-27T12:00"),
            meal = "saucisson brioché salade"
          ),
          Meal(
            id = UUID.fromString("050aacbd-4de9-4d56-abbc-2e64d613e9f8"),
            time = LocalDateTime.parse("2020-02-28T20:00"),
            meal = "chou-fleur pomme de terre lardons"
          )
        )
      )
    )
    private val february = monthFormatter.format(from)
    private val march = monthFormatter.format(to)

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

  it should "be displayed for each day of next week" in new WithRepositoryAndService {
    private val from = LocalDateTime.parse("2020-03-02T00:00")
    private val to = LocalDateTime.parse("2020-03-08T23:59:59.999999999")
    when(mealRepository.meals(from, to)).thenReturn(
      Future
        .successful(
          Seq(
            Meal(
              id = UUID.fromString("40cd80f6-bb5a-4b87-bcd9-3f475f6e3e4d"),
              time = LocalDateTime.parse("2020-03-02T12:00"),
              meal = "galettes de blé noir"
            ),
            Meal(
              id = UUID.fromString("ca73207d-f879-4ebf-9965-70ee732d136c"),
              time = LocalDateTime.parse("2020-03-03T20:00"),
              meal = "chipolatas pâtes"
            ),
            Meal(
              id = UUID.fromString("ca82f285-a88a-4e95-927a-a126f0de92d7"),
              time = LocalDateTime.parse("2020-03-04T20:00"),
              meal = "ratatouille riz"
            ),
            Meal(
              id = UUID.fromString("5480e229-6a56-4eb1-8271-d00dba2e72e0"),
              time = LocalDateTime.parse("2020-03-07T12:00"),
              meal = "lentilles saucisses (Morteau, Montbelliard) carottes"
            ),
            Meal(
              id = UUID.fromString("28389f39-3772-4ac2-b992-565af57160c2"),
              time = LocalDateTime.parse("2020-03-08T12:00"),
              meal = "quenelles riz sauce tomate"
            )
          )
        )
    )

    private val march = monthFormatter.format(from)
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

  it should "randomly choose a meal" in new WithRepositoryAndService {
    private val day = LocalDateTime.parse("2020-03-02T20:00")
    when(mealRepository.all()).thenReturn(
      MealsServiceSpec
        .AllResponse(
          Seq(
            "monday next week                          ->                   2020-03-02T20:00",
            "monday current week and tuesday next week -> 2020-02-24T20:00, 2020-03-03T20:00",
            "tuesday current week                      -> 2020-02-25T20:00",
            "wednesday current week                    -> 2020-02-26T20:00"
          )
        )
        .toFuture
    )
    when(mealRepository.link(any[MealRow], eqTo(day))).thenAnswer(answer { (mealRow: MealRow, day: LocalDateTime) =>
      Future.successful(Meal(mealRow.id, day, mealRow.description))
    })

    whenReady(mealsService.shuffle(day)) { meal =>
      meal.value.meal should (be("tuesday current week") or be("wednesday current week"))
    }
  }

  it should "suggest meals ordered by most recent" in new WithRepositoryAndService {
    when(mealRepository.all()).thenReturn(
      MealsServiceSpec
        .AllResponse(
          Seq(
            "salade tomates concombres -> 2020-02-17T12:00, 2020-02-18T12:00",
            "tomates farcies           -> 2020-02-17T20:00",
            "pâtes sauce tomate        -> 2020-02-19T12:00",
            "ratatouille               -> 2020-02-20T12:00"
          )
        )
        .toFuture
    )

    whenReady(mealsService.suggest(reference, None)) { suggests =>
      suggests.mostRecents should contain inOrderOnly (
        MealSuggest(2, "salade tomates concombres", "salade tomates concombres", 11),
        MealSuggest(1, "ratatouille", "ratatouille", 9),
        MealSuggest(1, "pâtes sauce tomate", "pâtes sauce tomate", 10)
      )
    }
  }

  it should "suggest highlighted meals filtered by a search" in new WithRepositoryAndService {
    when(mealRepository.all()).thenReturn(
      MealsServiceSpec
        .AllResponse(
          Seq(
            "salade tomates concombres -> 2020-02-17T12:00, 2020-02-18T12:00",
            "tomates farcies           -> 2020-02-17T20:00",
            "pâtes sauce tomate        -> 2020-02-19T12:00",
            "ratatouille               -> 2020-02-20T12:00"
          )
        )
        .toFuture
    )

    whenReady(mealsService.suggest(reference, Some("omat"))) { suggests =>
      suggests.mostRecents should contain inOrderOnly (
        MealSuggest(2, "salade tomates concombres", "salade t<strong>omat</strong>es concombres", 11),
        MealSuggest(1, "pâtes sauce tomate", "pâtes sauce t<strong>omat</strong>e", 10)
      )
    }
  }

  it should "suggest four weeks ago meal" in new WithRepositoryAndService {
    when(mealRepository.all()).thenReturn(
      MealsServiceSpec
        .AllResponse(
          Seq(
            "salade tomates concombres -> 2020-02-01T12:00, 2020-02-18T12:00",
            "tomates farcies           -> 2020-02-17T20:00",
            "pâtes sauce tomate        -> 2020-02-19T12:00",
            "ratatouille               -> 2020-02-20T12:00"
          )
        )
        .toFuture
    )

    whenReady(mealsService.suggest(reference, None)) { suggests =>
      suggests.fourWeeksAgo.value should be(
        MealSuggest(2, "salade tomates concombres", "salade tomates concombres", 11)
      )
      suggests.mostRecents should contain inOrderOnly (
        MealSuggest(1, "ratatouille", "ratatouille", 9),
        MealSuggest(1, "pâtes sauce tomate", "pâtes sauce tomate", 10)
      )
    }
  }

  it should "suggest fifty two weeks ago meal" in new WithRepositoryAndService {
    when(mealRepository.all()).thenReturn(
      MealsServiceSpec
        .AllResponse(
          Seq(
            "salade tomates concombres -> 2019-03-02T12:00, 2020-02-18T12:00",
            "tomates farcies           -> 2020-02-17T20:00",
            "pâtes sauce tomate        -> 2020-02-19T12:00",
            "ratatouille               -> 2020-02-20T12:00"
          )
        )
        .toFuture
    )

    whenReady(mealsService.suggest(reference, None)) { suggests =>
      suggests.fiftyTwoWeeksAgo.value should be(
        MealSuggest(2, "salade tomates concombres", "salade tomates concombres", 11)
      )
      suggests.mostRecents should contain inOrderOnly (
        MealSuggest(1, "ratatouille", "ratatouille", 9),
        MealSuggest(1, "pâtes sauce tomate", "pâtes sauce tomate", 10)
      )
    }
  }

  private trait WithRepositoryAndService {
    val mealRepository: MealRepository = mock(classOf[MealRepository])
    private val clock: Clock = Clock.fixed(Instant.parse("2020-02-29T11:00:00Z"), ZoneId.of("Europe/Paris"))
    val mealsService: MealsService = new MealsService(clock = clock, repository = mealRepository)
    val reference: LocalDateTime = clock.instant().atZone(clock.getZone).toLocalDateTime
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
