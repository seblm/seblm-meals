package meals

import akka.stream.Materializer
import meals.application.MealsComponents
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.components.OneAppPerSuiteWithComponents
import play.api.BuiltInComponents

import java.time.Clock
import scala.concurrent.ExecutionContext

trait MealsPlaySpec extends PlaySpec with OneAppPerSuiteWithComponents {

  implicit lazy val materializer: Materializer = app.materializer
  implicit lazy val ec: ExecutionContext = app.actorSystem.dispatcher

  lazy val mealsComponents: MealsComponents =
    clock().fold(new MealsComponents(context))(clock => new MealsComponents(context, clock))

  def clock(): Option[Clock] = None

  override lazy val components: BuiltInComponents = mealsComponents

}
