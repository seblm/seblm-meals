package meals.application

import meals.MealsPlaySpec
import meals.application.LinkOrInsertDataWrites.given
import meals.application.UnlinkMealWrites.given
import meals.domain.WeekMealsReads.given
import meals.domain.WeekMealsCenteredAroundADayReads.given
import meals.domain.*
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.*

import java.nio.file.{Files, Paths}
import java.time.*

class MealsControllerSpec extends MealsPlaySpec:

  override def clock(): Option[Clock] =
    Some(Clock.fixed(Instant.parse("2023-01-19T18:54:55.716650Z"), ZoneId.of("Europe/Paris")))

  "MealsController" should:
    "create, get around a date, get statistics and delete meals" in:
      val pizzaTime = LocalDateTime.parse("2023-01-16T12:00:00")
      val pizza = LinkOrInsertData("pizza", pizzaTime)
      val pastaTime = LocalDateTime.parse("2023-01-17T20:00:00")
      val pasta = LinkOrInsertData("pasta", pastaTime)

      Vector(pizza, pasta).foreach: meal =>
        val result = call(mealsComponents.mealsController.linkOrInsertApi(), FakeRequest().withBody(Json.toJson(meal)))
        status(result) must be(CREATED)

      val mealsAround = call(mealsComponents.mealsController.mealsAround(17, 1, Year.of(2023)), FakeRequest())
      val mealsAroundResponse = Json.fromJson[WeekMealsCenteredAroundADay](contentAsJson(mealsAround)).asOpt.value
      mealsAroundResponse.days must have size 7
      mealsAroundResponse.days.headOption.value must be(WeekDay(LocalDate.parse("2023-01-14"), None, None))
      mealsAroundResponse.days.lift(1).value must be(WeekDay(LocalDate.parse("2023-01-15"), None, None))
      mealsAroundResponse.days.lift(2).value.reference must be(LocalDate.parse("2023-01-16"))
      val pizzaMealEntry = mealsAroundResponse.days.lift(2).value.lunch.value
      val pizzaId = pizzaMealEntry.meal.id
      pizzaMealEntry.time must be(pizzaTime)
      pizzaMealEntry.meal.description must be("pizza")
      pizzaMealEntry.meal.url must not be defined
      pizzaMealEntry.meal.image must not be defined
      mealsAroundResponse.days.lift(2).value.dinner must not be defined
      mealsAroundResponse.days.lift(3).value.reference must be(LocalDate.parse("2023-01-17"))
      mealsAroundResponse.days.lift(3).value.lunch must not be defined
      val pastaMealEntry = mealsAroundResponse.days.lift(3).value.dinner.value
      val pastaId = pastaMealEntry.meal.id
      pastaMealEntry.time must be(pastaTime)
      pastaMealEntry.meal.description must be("pasta")
      pastaMealEntry.meal.url must not be defined
      pastaMealEntry.meal.image must not be defined
      mealsAroundResponse.days.lift(4).value must be(WeekDay(LocalDate.parse("2023-01-18"), None, None))
      mealsAroundResponse.days.lift(5).value must be(WeekDay(LocalDate.parse("2023-01-19"), None, None))
      mealsAroundResponse.days.lift(6).value must be(WeekDay(LocalDate.parse("2023-01-20"), None, None))

      val pastaImage = Paths.get("target", "scala-3.9.0", "classes", "assets", s"$pastaId.webp")
      Files.createFile(pastaImage)

      val mealsAroundWithImage = call(mealsComponents.mealsController.mealsAround(17, 1, Year.of(2023)), FakeRequest())
      val mealsAroundResponseWithImage =
        Json.fromJson[WeekMealsCenteredAroundADay](contentAsJson(mealsAroundWithImage)).asOpt.value
      mealsAroundResponseWithImage.days.lift(3).value.dinner.value.meal.image.value must be(s"/assets/$pastaId.webp")

      val all = call(mealsComponents.mealsController.mealsStatistics(), FakeRequest())
      val allResponse = Json.fromJson[Vector[MealStatistics]](contentAsJson(all)).asOpt.value
      allResponse must contain inOrderOnly (
        MealStatistics(1, pastaTime, pastaTime, Meal(pastaId, "pasta", None, Some(s"/assets/$pastaId.webp"))),
        MealStatistics(1, pizzaTime, pizzaTime, Meal(pizzaId, "pizza", None, None))
      )

      Files.delete(pastaImage)
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
