package meals.application

import controllers.AssetsComponents
import meals.application.MealsController
import meals.domain.{MealRepository, MealsService}
import meals.infrastructure.MealsDAO
import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.api.db.evolutions.EvolutionsComponents
import play.api.db.slick.evolutions.SlickEvolutionsComponents
import play.api.db.slick.{DbName, SlickComponents, SlickModule}
import play.api.routing.Router
import play.filters.HttpFiltersComponents
import router.Routes
import slick.jdbc.JdbcProfile

import java.time.{Clock, ZoneId}

class MealsComponents(context: Context)
    extends BuiltInComponentsFromContext(context)
    with AssetsComponents
    with EvolutionsComponents
    with HttpFiltersComponents
    with SlickComponents
    with SlickEvolutionsComponents {

  lazy val mealRepository: MealRepository = new MealsDAO(
    slickApi.dbConfig[JdbcProfile](DbName(configuration.get[String](SlickModule.DefaultDbName))),
    controllerComponents
  )

  lazy val router: Router = {
    val clock = Clock.system(ZoneId.of("Europe/Paris"))
    val mealsService = new MealsService(clock, mealRepository)
    new Routes(httpErrorHandler, new MealsController(controllerComponents, mealsService), assets)
  }

  // this will actually run the database migrations on startup
  applicationEvolutions

}
