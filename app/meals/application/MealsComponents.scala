package meals.application

import controllers.AssetsComponents
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

class MealsComponents(context: Context, clock: Clock = Clock.system(ZoneId.of("Europe/Paris")))
    extends BuiltInComponentsFromContext(context)
    with AssetsComponents
    with EvolutionsComponents
    with HttpFiltersComponents
    with SlickComponents
    with SlickEvolutionsComponents {

  lazy val mealRepository: MealRepository =
    new MealsDAO(slickApi.dbConfig[JdbcProfile](DbName(configuration.get[String](SlickModule.DefaultDbName))))
  private lazy val mealsService = new MealsService(clock, mealRepository)
  lazy val mealsController: MealsController = new MealsController(controllerComponents, mealsService)

  lazy val router: Router = new Routes(httpErrorHandler, mealsController, assets)

  // this will actually run the database migrations on startup
  applicationEvolutions

}
