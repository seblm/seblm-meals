package meals.application

import controllers.AssetsComponents
import meals.application.MealsController
import meals.domain.{MealRepository, MealsService, MealsServiceImpl}
import meals.infrastructure.MealsDAO
import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.api.db.evolutions.EvolutionsComponents
import play.api.db.slick.evolutions.SlickEvolutionsComponents
import play.api.db.slick.{DbName, SlickComponents, SlickModule}
import play.api.routing.Router
import play.filters.HttpFiltersComponents
import router.Routes
import slick.basic.{BasicProfile, DatabaseConfig}
import slick.jdbc.{JdbcProfile, PostgresProfile}

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
    val clock: Clock = Clock.system(ZoneId.of("Europe/Paris"))
    val mealsService: MealsService = new MealsServiceImpl(clock, mealRepository)
    new Routes(httpErrorHandler, new MealsController(controllerComponents, mealsService), assets)
  }

  // this will actually run the database migrations on startup
  applicationEvolutions

}
