package meals.application

import controllers.Assets
import play.api.mvc.RequestHeader
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter

class SPARouter(assets: Assets) extends SimpleRouter:

  override def routes: Routes = (_: RequestHeader) => assets.at("/200.html")
