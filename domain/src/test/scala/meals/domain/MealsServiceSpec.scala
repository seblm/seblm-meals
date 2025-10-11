package meals.domain

import meals.domain.*
import org.mockito.Mockito.{mock, when}
import org.scalatest.OptionValues.*
import org.scalatest.concurrent.Futures.scaled
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.time.{Millis, Span}

import java.time.*
import java.util.UUID
import scala.concurrent.ExecutionContext.global
import scala.concurrent.{ExecutionContext, Future}

class MealsServiceSpec extends AnyFlatSpec with Eventually with ScalaFutures:

  given ExecutionContext = global
  override given patienceConfig: PatienceConfig = PatienceConfig(timeout = scaled(Span(300, Millis)))

  "Meals" should "be displayed for each day of current week" in new WithRepositoryAndService() {
    private val from = LocalDateTime.parse("2020-02-24T00:00")
    private val to = LocalDateTime.parse("2020-03-01T23:59:59.999999999")
    when(mealRepository.meals(from, to)).thenReturn(
      Future.successful(
        Seq(
          MealEntry(
            meal = Meal(
              id = UUID.fromString("8ffaf9c5-f9a4-4d7d-8358-d65f17882a2a"),
              description = "saucisson brioché salade",
              url = Some("https://seblm.github.io")
            ),
            time = LocalDateTime.parse("2020-02-27T12:00")
          ),
          MealEntry(
            meal = Meal(
              id = UUID.fromString("050aacbd-4de9-4d56-abbc-2e64d613e9f8"),
              description = "chou-fleur pomme de terre lardons",
              url = None
            ),
            time = LocalDateTime.parse("2020-02-28T20:00")
          )
        )
      )
    )
    whenReady(mealsService.meals(Year.of(2020), 9)) { weekMeals =>
      weekMeals.titles.short shouldBe "2020 semaine n°09"
      weekMeals.titles.long shouldBe s"Semaine n°09 - du lundi 24 février au dimanche 1 mars 2020"
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
      weekMeals.thursday.lunch.value.meal.description shouldBe "saucisson brioché salade"
      weekMeals.thursday.lunch.value.meal.url.value shouldBe "https://seblm.github.io"
      weekMeals.thursday.dinner should not be defined
      weekMeals.friday.lunch should not be defined
      weekMeals.friday.dinner.value.meal.description shouldBe "chou-fleur pomme de terre lardons"
      weekMeals.friday.dinner.value.meal.url shouldBe empty
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
            MealEntry(
              meal = Meal(
                id = UUID.fromString("40cd80f6-bb5a-4b87-bcd9-3f475f6e3e4d"),
                description = "galettes de blé noir",
                url = None
              ),
              time = LocalDateTime.parse("2020-03-02T12:00")
            ),
            MealEntry(
              meal = Meal(
                id = UUID.fromString("ca73207d-f879-4ebf-9965-70ee732d136c"),
                description = "chipolatas pâtes",
                url = None
              ),
              time = LocalDateTime.parse("2020-03-03T20:00")
            ),
            MealEntry(
              meal = Meal(
                id = UUID.fromString("ca82f285-a88a-4e95-927a-a126f0de92d7"),
                description = "ratatouille riz",
                url = None
              ),
              time = LocalDateTime.parse("2020-03-04T20:00")
            ),
            MealEntry(
              meal = Meal(
                id = UUID.fromString("5480e229-6a56-4eb1-8271-d00dba2e72e0"),
                description = "lentilles saucisses (Morteau, Montbelliard) carottes",
                url = None
              ),
              time = LocalDateTime.parse("2020-03-07T12:00")
            ),
            MealEntry(
              meal = Meal(
                id = UUID.fromString("28389f39-3772-4ac2-b992-565af57160c2"),
                description = "quenelles riz sauce tomate",
                url = None
              ),
              time = LocalDateTime.parse("2020-03-08T12:00")
            )
          )
        )
    )

    whenReady(mealsService.meals(Year.of(2020), 10)) { weekMeals =>
      weekMeals.titles.short shouldBe "2020 semaine n°10"
      weekMeals.titles.long shouldBe s"Semaine n°10 - du lundi 2 au dimanche 8 mars 2020"
      weekMeals.previous.year shouldBe Year.of(2020)
      weekMeals.previous.week shouldBe 8
      weekMeals.previous.isActive shouldBe false
      weekMeals.now.year shouldBe Year.of(2020)
      weekMeals.now.week shouldBe 9
      weekMeals.now.isActive shouldBe false
      weekMeals.next.year shouldBe Year.of(2020)
      weekMeals.next.week shouldBe 10
      weekMeals.next.isActive shouldBe true
      weekMeals.monday.lunch.value.meal.description shouldBe "galettes de blé noir"
      weekMeals.monday.dinner should not be defined
      weekMeals.tuesday.lunch should not be defined
      weekMeals.tuesday.dinner.value.meal.description shouldBe "chipolatas pâtes"
      weekMeals.wednesday.lunch should not be defined
      weekMeals.wednesday.dinner.value.meal.description shouldBe "ratatouille riz"
      weekMeals.thursday.lunch should not be defined
      weekMeals.thursday.dinner should not be defined
      weekMeals.friday.lunch should not be defined
      weekMeals.friday.dinner should not be defined
      weekMeals.saturday.lunch.value.meal.description shouldBe "lentilles saucisses (Morteau, Montbelliard) carottes"
      weekMeals.saturday.dinner should not be defined
      weekMeals.sunday.lunch.value.meal.description shouldBe "quenelles riz sauce tomate"
      weekMeals.sunday.dinner should not be defined
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

  it should "suggest case insensitive meals" in new WithRepositoryAndService {
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

    whenReady(mealsService.suggest(reference, Some("OMaT"))) { suggests =>
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

  private trait WithRepositoryAndService:
    val mealRepository: MealRepository = mock(classOf[MealRepository])
    private val clock: Clock = Clock.fixed(Instant.parse("2020-02-29T11:00:00Z"), ZoneId.of("Europe/Paris"))
    val mealsService: MealsService = new MealsService(clock = clock, repository = mealRepository)
    val reference: LocalDateTime = clock.instant().atZone(clock.getZone).toLocalDateTime

object MealsServiceSpec:

  case class AllResponse(all: Seq[String])

  extension (allResponse: AllResponse)

    def toFuture: Future[Map[Meal, Seq[LocalDateTime]]] =
      Future.successful(toAllResponseMap(allResponse))

    private def toAllResponseMap(all: AllResponse): Map[Meal, Seq[LocalDateTime]] = all.all match
      case Nil          => Map.empty
      case last :: Nil  => Map(toAllResponseMap(last))
      case head :: tail => Map(toAllResponseMap(head)) ++ toAllResponseMap(AllResponse(tail))

    private def toAllResponseMap(line: String): (Meal, Seq[LocalDateTime]) = line.split("->") match
      case Array(description, dates) =>
        Meal(UUID.randomUUID(), description.trim(), None) -> dates
          .split(",")
          .toIndexedSeq
          .map(x => LocalDateTime.parse(x.trim))
