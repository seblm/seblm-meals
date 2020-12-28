import java.time.{Clock, ZoneId}
import com.google.inject.AbstractModule
import meals.domain.{MealRepository, MealsService, MealsServiceImpl}
import meals.infrastructure.MealsDAO

import scala.concurrent.ExecutionContext

class Module extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[MealRepository]).to(classOf[MealsDAO]).asEagerSingleton()
    bind(classOf[Clock]).toInstance(Clock.system(ZoneId.of("Europe/Paris")))
    bind(classOf[MealsService])
      .toConstructor(
        classOf[MealsServiceImpl].getConstructor(classOf[Clock], classOf[MealRepository], classOf[ExecutionContext])
      )
      .asEagerSingleton()
    ()
  }

}
