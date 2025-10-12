package meals.application

import meals.MealsPlaySpec
import meals.application.LinkOrInsertDataWrites.given
import meals.application.UnlinkMealWrites.given
import meals.domain.WeekMealsReads.given
import meals.domain.*
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.*

import java.time.*
import java.util.UUID

class MealsControllerSpec extends MealsPlaySpec:

  override def clock(): Option[Clock] =
    Some(Clock.fixed(Instant.parse("2023-01-19T18:54:55.716650Z"), ZoneId.of("Europe/Paris")))

  "MealsController" should:
    "get all meals and around a date" in:
      val pizzaTime = LocalDateTime.parse("2023-01-16T12:00:00")
      val pizza = LinkOrInsertData("pizza", pizzaTime)
      val pastaTime = LocalDateTime.parse("2023-01-17T20:00:00")
      val pasta = LinkOrInsertData("pasta", pastaTime)

      Vector(pizza, pasta).foreach: meal =>
        val result = call(mealsComponents.mealsController.linkOrInsertApi(), FakeRequest().withBody(Json.toJson(meal)))
        status(result) must be(CREATED)

      val id = UUID.fromString("7f209aff-2aae-4bf4-ba5d-f7741cfe7c07")
      val all = call(mealsComponents.mealsController.meals(), FakeRequest())
      val allResponse = Json.fromJson[Vector[MealStatistics]](contentAsJson(all)).asOpt.value
      val allResponseWithoutMealIds =
        allResponse.map(mealStatistics => mealStatistics.copy(meal = mealStatistics.meal.copy(id = id)))
      allResponseWithoutMealIds must contain inOrderOnly (
        MealStatistics(1, pastaTime, pastaTime, Meal(id, "pasta", None)),
        MealStatistics(1, pizzaTime, pizzaTime, Meal(id, "pizza", None))
      )

      val result = call(mealsComponents.mealsController.mealsAround(LocalDate.parse("2023-01-16"), 3), FakeRequest())

      val response = Json.fromJson[Vector[WeekDay]](contentAsJson(result)).asOpt.value
      response.map(_.reference) must contain only (LocalDate.parse("2023-01-16"), LocalDate.parse("2023-01-17"))

      Vector(pizza, pasta).foreach: meal =>
        val unlinkRequest = FakeRequest().withMethod("DELETE").withBody(Json.toJson(UnlinkMeal(meal.mealTime)))
        val result = call(mealsComponents.mealsController.unlinkApi(), unlinkRequest)
        status(result) must be(NO_CONTENT)

    "get meals for a week" in:
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

    "insert, unlink and link a meal" in:
      val mealTime = LocalDateTime.parse("2023-09-17T12:00:00")
      val pizza = FakeRequest().withBody(Json.toJson(LinkOrInsertData("pizza", mealTime)))
      val pasta = FakeRequest().withBody(Json.toJson(LinkOrInsertData("pasta", mealTime)))

      val inserted = call(mealsComponents.mealsController.linkOrInsertApi(), pizza)
      status(inserted) must be(CREATED)
      val mealsInserted = call(mealsComponents.mealsController.mealsApi(Year.of(2023), 37), FakeRequest())
      Json.fromJson[WeekMeals](contentAsJson(mealsInserted)).asOpt.value.sunday.lunch.value.meal.description must
        be("pizza")

      val deleted =
        call(mealsComponents.mealsController.unlinkApi(), FakeRequest().withBody(Json.toJson(UnlinkMeal(mealTime))))
      status(deleted) must be(NO_CONTENT)
      val mealsDeleted = call(mealsComponents.mealsController.mealsApi(Year.of(2023), 37), FakeRequest())
      Json.fromJson[WeekMeals](contentAsJson(mealsDeleted)).asOpt.value.sunday.lunch must be(empty)

      val linked = call(mealsComponents.mealsController.linkOrInsertApi(), pizza)
      status(linked) must be(CREATED)
      val mealsLinked = call(mealsComponents.mealsController.mealsApi(Year.of(2023), 37), FakeRequest())
      Json.fromJson[WeekMeals](contentAsJson(mealsLinked)).asOpt.value.sunday.lunch.value.meal.description must
        be("pizza")

      val otherInserted = call(mealsComponents.mealsController.linkOrInsertApi(), pasta)
      status(otherInserted) must be(CREATED)
      val otherMealsLinked = call(mealsComponents.mealsController.mealsApi(Year.of(2023), 37), FakeRequest())
      Json.fromJson[WeekMeals](contentAsJson(otherMealsLinked)).asOpt.value.sunday.lunch.value.meal.description must
        be("pasta")
