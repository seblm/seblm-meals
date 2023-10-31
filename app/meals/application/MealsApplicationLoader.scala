package meals.application

import play.api.ApplicationLoader.Context
import play.api.{Application, ApplicationLoader, LoggerConfigurator}

class MealsApplicationLoader extends ApplicationLoader:

  override def load(context: Context): Application =
    LoggerConfigurator(context.environment.classLoader)
      .foreach(_.configure(context.environment, context.initialConfiguration, Map.empty))
    new MealsComponents(context).application
