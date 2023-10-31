package meals.application

import meals.MealsPlaySpec
import meals.domain.WeekMealsReads.*
import meals.domain.{Titles, WeekDay, WeekMeals, WeekReference}
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import meals.application.UnlinkMealWrites.*

import java.time.*

class MealsControllerSpec extends MealsPlaySpec:

  override def clock(): Option[Clock] =
    Some(Clock.fixed(Instant.parse("2023-01-19T18:54:55.716650Z"), ZoneId.of("Europe/Paris")))

  "MealsController" should {
    "get meals for a week" in {
      val result = call(mealsComponents.mealsController.mealsApi(Year.of(2023), 3), FakeRequest())

      val response = Json.fromJson[WeekMeals](contentAsJson(result)).asOpt.value
      response must be(
        WeekMeals(
          Titles("2023 semaine n°03", "Semaine n°03 - du lundi 16 au dimanche 22 janvier 2023"),
          previous = WeekReference(Year.of(2023), 2, isActive = false),
          now = WeekReference(Year.of(2023), 3, isActive = true),
          next = WeekReference(Year.of(2023), 4, isActive = false),
          monday = WeekDay(LocalDate.parse("2023-01-16"), None, None),
          tuesday = WeekDay(LocalDate.parse("2023-01-17"), None, None),
          wednesday = WeekDay(LocalDate.parse("2023-01-18"), None, None),
          thursday = WeekDay(LocalDate.parse("2023-01-19"), None, None),
          friday = WeekDay(LocalDate.parse("2023-01-20"), None, None),
          saturday = WeekDay(LocalDate.parse("2023-01-21"), None, None),
          sunday = WeekDay(LocalDate.parse("2023-01-22"), None, None)
        )
      )
    }

    "insert, unlink and link a meal" in {
      import meals.application.LinkOrInsertDataWrites._

      val mealTime = LocalDateTime.parse("2023-09-17T12:00:00")
      val pizza = FakeRequest().withBody(Json.toJson(LinkOrInsertData("pizza", mealTime)))
      val pasta = FakeRequest().withBody(Json.toJson(LinkOrInsertData("pasta", mealTime)))

      val inserted = call(mealsComponents.mealsController.linkOrInsertApi(), pizza)
      status(inserted) must be(CREATED)
      val mealsInserted = call(mealsComponents.mealsController.mealsApi(Year.of(2023), 37), FakeRequest())
      Json.fromJson[WeekMeals](contentAsJson(mealsInserted)).asOpt.value.sunday.lunch.value.meal must be("pizza")

      val deleted =
        call(mealsComponents.mealsController.unlinkApi(), FakeRequest().withBody(Json.toJson(UnlinkMeal(mealTime))))
      status(deleted) must be(NO_CONTENT)
      val mealsDeleted = call(mealsComponents.mealsController.mealsApi(Year.of(2023), 37), FakeRequest())
      Json.fromJson[WeekMeals](contentAsJson(mealsDeleted)).asOpt.value.sunday.lunch must be(empty)

      val linked = call(mealsComponents.mealsController.linkOrInsertApi(), pizza)
      status(linked) must be(CREATED)
      val mealsLinked = call(mealsComponents.mealsController.mealsApi(Year.of(2023), 37), FakeRequest())
      Json.fromJson[WeekMeals](contentAsJson(mealsLinked)).asOpt.value.sunday.lunch.value.meal must be("pizza")

      val otherInserted = call(mealsComponents.mealsController.linkOrInsertApi(), pasta)
      status(otherInserted) must be(CREATED)
      val otherMealsLinked = call(mealsComponents.mealsController.mealsApi(Year.of(2023), 37), FakeRequest())
      Json.fromJson[WeekMeals](contentAsJson(otherMealsLinked)).asOpt.value.sunday.lunch.value.meal must be("pasta")
    }

  }
